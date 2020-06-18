package ds.photosight.parser

import org.junit.Before
import org.junit.Test

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
    fun `comments showcase`() {
        PhotoDetailsRequest(5358008)()
            .also { println(it) }
    }


}