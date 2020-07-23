package ds.photosight.parser

import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class PhotosightParserTest {

    @Before
    fun setup() {
        debugEnabled = true
    }

    @Test
    fun `get categories`() {
        CategoriesRequest()()
            .forEach { println(it) }
    }

    @Test
    fun `simple category fetch`() {
        CategoriesPhotosRequest(15, 1)()
            .forEach { println(it) }
    }

    @Test
    fun `category sorting`() {
        CategoriesPhotosRequest(15, 1, CategoriesPhotosRequest.SortDumpCategory.ALL, CategoriesPhotosRequest.SortTypeCategory.COMMENTS_COUNT)()
            .forEach { println(it) }
    }

    @Test
    fun `top week`() {
        Top50PhotosRequest()()
            .forEach { println(it) }
    }

    @Test
    fun `best photos`() {
        BestPhotosRequest()()
            .forEach { println(it) }
    }

    @Test
    fun `outrun photos`() {
        OutrunPhotosRequest()()
            .forEach { println(it) }
    }

    @Test
    fun `daily outrun photos`() {
        DailyPhotosRequest(2013, 12, 31, 15)()
            .forEach { println(it) }
    }

    @Test
    fun `date page with index 1 should aim on today`() {
        val datePage = DailyPhotosRequest.DatePage.fromPage(1)
        println("${datePage.year}/${datePage.month}/${datePage.day}")
        val now = DailyPhotosRequest.DatePage.now
        assertEquals(1, datePage.page)
        assertEquals(now.get(Calendar.YEAR), datePage.year)
        assertEquals(now.get(Calendar.MONTH), datePage.month - 1)
        assertEquals(now.get(Calendar.DAY_OF_MONTH), datePage.day)
    }

    @Test
    fun `comments showcase`() {
        PhotoDetailsRequest(5358008)()
            .also { println(it) }
    }


}