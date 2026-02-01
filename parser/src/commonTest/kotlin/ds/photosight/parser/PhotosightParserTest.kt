package ds.photosight.parser

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class PhotosightParserTest {

    @BeforeTest
    fun setup() {
        debugEnabled = true
    }

    @Test
    fun `get categories`() {
        runTest {
            val categories = CategoriesRequest()()
            categories.forEach { println(it) }
        }
    }

    @Test
    fun `comments showcase`() {
        runTest {
            val details = PhotoDetailsRequest(7249211)()
            println(details)
        }
    }

    @Test
    fun `get photos and check url transformation`() {
        runTest {
            val page = NewPhotosRequest(SimplePage(1))()
            println("Found ${page.photos.size} photos")
            page.photos.take(3).forEach { photo ->
                println("ID: ${photo.id}")
                println("Thumb: ${photo.thumb}")
                println("Large: ${photo.large}")
                println("Title: ${photo.title}")
                println("---")
            }
        }
    }
}
