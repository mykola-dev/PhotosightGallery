package ds.photosight.parser

import ds.photosight.http_client.runHttpRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

interface Request<T> {
    operator fun invoke(): T
    val url: String
    val cookies: Map<String, String>
}

abstract class JsoupRequest<T> : Request<T> {
    @Deprecated("not relevant")
    private val nudeModeCookie: Pair<String, String> = "show_nude" to "1"

    @Deprecated("not relevant")
    private val adultModeCookie: Pair<String, String> = "adult_mode" to "1"

    @Deprecated("not relevant")
    private val categoryDescriptionCookie: Pair<String, String> = "show_category_description" to "0"
    protected val baseUrl = "https://sight.photo"

    protected fun Document.parse(selector: String): Elements {
        val elements = select(selector)
        if (debugEnabled) {
            println("url=$url")
            println(elements)
        }
        return elements
    }

    protected fun getDocument(): Document = Jsoup.parse(runHttpRequest(url))

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

class PhotoDetailsRequest(photoId: Int) : JsoupRequest<PhotoDetails>() {

    private val dateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm:ss", Locale.US)

    override fun invoke(): PhotoDetails {
        val doc = getDocument()
        val comments = doc
            .commentsSection()
            .map { comment ->
                val text = comment.select("div.right-part > p").text()
                val dateRaw = comment.select("span.date").text()
                val (avatar, author) = comment
                    .getElementsByClass("avatar")
                    .first()!!
                    .getElementsByTag("img")
                    .run { attr("src") to attr("alt") }
                val likes = comment.select("span.count").text().ifEmpty { "0" }.toInt()
                val isAuthor = comment.hasClass("author")
                val timestamp = LocalDateTime.parse(dateRaw, dateFormat).atZone(ZoneId.systemDefault()).toInstant()
                PhotoDetails.Comment(text, dateRaw, timestamp, author, avatar, likes, isAuthor)
            }

        val awards = doc
            .awardsSection()
            .map { e -> e.classNames().first { it != "medal" } }
            .mapNotNull { PhotoDetails.Award.fromString(it) }

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

        return PhotoDetails(comments, awards, stats)
    }

    private fun Document.commentsSection() = parse("div.comments div.comment-content")
    private fun Document.awardsSection() = parse("div.medals > div.medal")
    private fun Document.infoSection() = parse("div.photo-info")

    override val url: String = "$baseUrl/photos/$photoId"

}

abstract class PhotosRequest : JsoupRequest<List<PhotoInfo>>() {

    override fun invoke(): List<PhotoInfo> = getDocument()
        .parse("div.photo-item")
        .map { el ->
            val id = el
                .getElementsByTag("a")
                .first()!!
                .attr("data-href")
                .let { Regex("/photos/(\\d+).*").matchEntire(it) }
                ?.groupValues
                ?.get(1)
                ?.toInt()
                ?: error("can't parse id")
            val (thumb, title) = el
                .getElementsByTag("img")
                .run { attr("src") to attr("alt").trim() }
            val large = thumb.thumbToLarge()
            val pageUrl = "$baseUrl/photos/$id"
            val (author, authorUrl) = el
                .getElementsByTag("p")
                .first()!!
                .let { e -> e.getElementsByTag("a").first() ?: e }
                .let { e -> e.text() to e.attr("abs:href").takeIf { it.isNotBlank() } }

            val paginationKey = (this as? Multipage)?.page?.key

            PhotoInfo(id, thumb, large, pageUrl, title, author, authorUrl, paginationKey)
        }

}

class DailyPhotosRequest(override val page: DatePage, category: Int? = null) : PhotosRequest(), Multipage {
    override val url: String = "$baseUrl/outrun/date/${page.key}/${category?.let { "?category=$it" } ?: ""}"
}

class CategoriesPhotosRequest(
    category: Int,
    override val page: SimplePage,
    sortDumpCategory: SortDumpCategory = SortDumpCategory.ALL,
    sortTypeCategory: SortTypeCategory = SortTypeCategory.DEFAULT
) : PhotosRequest(), Multipage {

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


    override val url: String = "https://photosight.ru/photos/category/$category?pager=${page.key}"

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

class NewPhotosRequest(override val page: SimplePage) : PhotosRequest(), Multipage {
    override val url: String = "$baseUrl/new_on_site/photos/?pager=${page.key}"
}

class PretenderPhotosRequest(override val page: SimplePage) : PhotosRequest(), Multipage {
    override val url: String = "$baseUrl/pretender/photos/?pager=${page.key}"
}

@Deprecated("use tops instead")
// pagination?
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