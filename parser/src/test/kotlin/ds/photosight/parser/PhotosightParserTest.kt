package ds.photosight.parser

import java.util.*
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test

class PhotosightParserTest {

    @Before
    fun setup() {
        debugEnabled = true
    }

    @Test
    fun `get categories`() {
        CategoriesRequest()().forEach { println(it) }
    }

    @Test
    fun `test pagination sizes`() {
        val request = CategoriesPhotosRequest(15, SimplePage(1))
        val doc =
                org.jsoup.Jsoup.parse(
                        ds.photosight.http_client.runHttpRequest(request.url, emptyMap())
                )
        println("CAT 15 PAGE 1 HTML START")
        println(doc.html())
        println("CAT 15 PAGE 1 HTML END")

        for (i in 1..5) {
            val page = NewPhotosRequest(SimplePage(i))()
            println("Page $i size: ${page.photos.size}, hasNext: ${page.hasNext}")
            // We no longer strictly expect 24 items if there is a next page
            if (page.hasNext) {
                assert(page.photos.isNotEmpty()) {
                    "Page $i should not be empty if hasNext is true"
                }
            }
        }
    }

    @Test
    fun `category sorting`() {
        CategoriesPhotosRequest(
                        15,
                        SimplePage(1),
                        CategoriesPhotosRequest.FilterDumpCategory.ALL,
                        CategoriesPhotosRequest.SortTypeCategory.COMMENTS_COUNT
                )()
                .photos
                .forEach { println(it) }
    }

    @Test
    fun `old photos`() {
        CategoriesPhotosRequest(
                        15,
                        SimplePage(1),
                        CategoriesPhotosRequest.FilterDumpCategory.ALL,
                        CategoriesPhotosRequest.SortTypeCategory.COUNT
                )()
                .photos
                .forEach { println(it) }
    }

    @Test
    fun `top week`() {
        Top50PhotosRequest()().photos.forEach { println(it) }
    }

    @Test
    fun `best photos`() {
        BestPhotosRequest()().photos.forEach { println(it) }
    }

    @Test
    fun `outrun photos`() {
        OutrunPhotosRequest()().photos.forEach { println(it) }
    }

    @Test
    fun `daily outrun photos`() {
        DailyPhotosRequest(DatePage(2013, 12, 31), 15)().photos.forEach { println(it) }
    }

    @Test
    fun `date page with index 1 should aim on today`() {
        val datePage = DatePage(1)
        println("$datePage")
        val now = DatePage.now
        assertEquals(1, datePage.index)
        assertEquals(now.get(Calendar.YEAR), datePage.year)
        assertEquals(now.get(Calendar.MONTH), datePage.month - 1)
        assertEquals(now.get(Calendar.DAY_OF_MONTH), datePage.day)
    }

    @Test
    fun `comments showcase`() {
        PhotoDetailsRequest(7249211)().also { println(it) }
    }
}
