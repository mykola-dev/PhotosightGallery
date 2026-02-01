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
}
