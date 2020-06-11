package ds.photosight_legacy.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.text.SimpleDateFormat
import java.util.*

interface Request<T> {
    operator fun invoke(): T
    val url: String
    val cookies: Map<String, String>
}

interface Multipage

abstract class JsoupRequest<T> : Request<T> {
    private val nudeModeCookie: Pair<String, String> = "show_nude" to "1"
    private val adultModeCookie: Pair<String, String> = "adult_mode" to "1"
    private val categoryDescriptionCookie: Pair<String, String> = "show_category_description" to "0"
    protected val baseUrl = "https://photosight.ru"

    protected fun Document.parse(selector: String): Elements {
        val elements = getDocument().select(selector)
        if (debugEnabled) println(elements)
        return elements
    }

    protected fun getDocument(): Document =
        Jsoup
            .connect(url)
            .cookies(cookies + nudeModeCookie + adultModeCookie + categoryDescriptionCookie)
            .get()

    override val cookies: Map<String, String> = emptyMap()
}

class CategoriesRequest : JsoupRequest<List<PhotoCategory>>() {
    private val photosCategoryPattern = Regex("""/photos/category/(\d+)/""")

    override fun invoke(): List<PhotoCategory> = getDocument()
        .parse("div.col > a[href~=/photos/category/.*]")
        .mapNotNull { e ->
            photosCategoryPattern
                .matchEntire(e.attr("href"))
                ?.groupValues
                ?.get(1)
                ?.toInt()
                ?.let { index ->
                    PhotoCategory(
                        index,
                        e.text()
                    )
                }
        }
        .sortedBy { it.index }

    override val url: String = baseUrl
}

class PhotoDetailsRequest(override val url: String) : JsoupRequest<PhotoDetails>() {
    private val originalDateFormat = "dd MMMM yyyy, kk:mm:ss"
    private val dateFormat = SimpleDateFormat(originalDateFormat, Locale("ru"))

    override fun invoke(): PhotoDetails {
        val doc = getDocument()
        val comments = doc
            .commentsSection()
            .map {
                val text = it.select("div.right-part > p").text()
                val dateRaw = it.select("span.date").text()
                val (avatar, author) = it
                    .getElementsByClass("avatar")
                    .first()
                    .getElementsByTag("img")
                    .run { attr("src") to attr("alt") }
                val likes = it.select("span.count").text().toInt()
                val isAuthor = it.hasClass("author")
                val timestamp = dateFormat.parse(dateRaw).time
                PhotoDetails.Comment(text, dateRaw, timestamp, author, avatar, likes, isAuthor)
            }
            .onEach { println(it) }

        // is_editors_choice, is_week_top, is_top, is_top_art, is_month_top_20, is_month_top_200, is_week_top_20, is_week_top_50
        val avards = doc
            .avardsSection()
            .map { e -> e.classNames().first { it != "medal" } }

        val stats = doc
            .infoSection()
            .let {
                val views = it.select("div.count").text().replace(Regex("\\D"), "").toInt()
                val art = it.select("div.item-x > span.count").text().toInt()
                val original = it.select("div.item-o > span.count").text().toInt()
                val tech = it.select("div.item-t > span.count").text().toInt()
                val likes = it.select("div.item-up > span.count").text().toInt()
                val dislikes = it.select("div.item-down > span.count").text().toInt()
                PhotoDetails.Stats(art, original, tech, likes, dislikes, views)
            }

        return PhotoDetails(comments, avards, stats)
    }

    private fun Document.commentsSection() = parse("div.comments div.comment-content")
    private fun Document.avardsSection() = parse("div.medals > div.medal")
    private fun Document.infoSection() = parse("div.photo-info")

}

abstract class PhotosRequest : JsoupRequest<List<PhotoInfo>>() {
    override fun invoke(): List<PhotoInfo> = getDocument()
        .parse("div.photo-item")
        .map {
            val id = it.attr("data-photoid").toInt()
            val (thumb, title) = it
                .getElementsByTag("img")
                .run { attr("src") to attr("alt").trim() }
            val large = thumb.thumbToLarge()
            val page = it.getElementsByTag("a").attr("abs:data-href")
            val (author, authorUrl) = it
                .getElementsByTag("p")
                .first()
                .let { e -> e.getElementsByTag("a").first() ?: e }
                .let { e -> e.text() to e.attr("abs:href") }

            PhotoInfo(id, thumb, large, page, title, author, authorUrl)
        }

}

class BestPhotosRequest(sort: Sort = Sort.BEST, time: Time = Time.DAY) : PhotosRequest() {

    enum class Sort(private val value: String) {
        BEST("best"),
        ART("art"),
        ORIG("orig"),
        TECH("tech");

        override fun toString(): String = value
    }

    enum class Time(private val value: String) {
        DAY("day"),
        WEEK("week"),
        MONTH("month");

        override fun toString(): String = value

    }

    override val url: String = "$baseUrl/best/?sort=$sort&time=$time"
}

class DailyPhotosRequest(year: Int, month: Int, day: Int, category: Int? = null) : PhotosRequest(), Multipage {
    override val url: String = "$baseUrl/outrun/date/$year/$month/$day/${category?.let { "?category=$it" } ?: ""}"
}

class CategoriesPhotosRequest(
    category: Int,
    currentPage: Int = 1,
    sortDumpCategory: SortDumpCategory = SortDumpCategory.ALL,
    sortTypeCategory: SortTypeCategory = SortTypeCategory.DEFAULT
) : PhotosRequest(), Multipage {
    init {
        if (currentPage < 1) error("currentPage must be > 0")
    }

    enum class SortDumpCategory(private val value: String) {
        ALL("all"),
        RATES("rates");

        override fun toString(): String = value
    }

    enum class SortTypeCategory(private val value: String) {
        DEFAULT("ctime"),
        COUNT("recommendations_count"),
        ART("recommendations_art"),
        ORIGINAL("recommendations_orig"),
        TECH("recommendations_tech"),
        COMMENTS_COUNT("comments_count");

        override fun toString(): String = value
    }


    override val url: String = "https://photosight.ru/photos/category/$category?pager=$currentPage"

    override val cookies: Map<String, String> = mapOf(
        "sort_dump_category" to sortDumpCategory.toString(),
        "sort_type_category" to sortTypeCategory.toString()
    )

}

class Top50PhotosRequest : PhotosRequest() {
    override val url: String = "$baseUrl/top/50"
}

class Top200PhotosRequest : PhotosRequest() {
    override val url: String = "$baseUrl/top/200"
}

class TopArtPhotosRequest : PhotosRequest() {
    override val url: String = "$baseUrl/top/art"
}

class TopTechPhotosRequest : PhotosRequest() {
    override val url: String = "$baseUrl/top/tech"
}

class TopOrigPhotosRequest : PhotosRequest() {
    override val url: String = "$baseUrl/top/orig"
}

class TopFavoritesPhotosRequest : PhotosRequest() {
    override val url: String = "$baseUrl/top/favorites"
}

class TopApplicantsPhotosRequest : PhotosRequest() {
    override val url: String = "$baseUrl/top/applicants"
}

class OutrunPhotosRequest : PhotosRequest() {
    override val url: String = "$baseUrl/outrun"
}