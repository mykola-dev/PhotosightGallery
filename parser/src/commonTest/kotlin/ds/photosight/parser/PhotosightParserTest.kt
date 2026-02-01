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

    @Test
    fun `test top 50 photos request`() {
        runTest {
            val page = Top50PhotosRequest()()
            println("Top50: Found ${page.photos.size} photos, hasNext=${page.hasNext}")
            page.photos.take(3).forEach { photo ->
                println("ID: ${photo.id}, Title: ${photo.title}")
            }
        }
    }

    @Test
    fun `test top 200 photos request`() {
        runTest {
            val page = Top200PhotosRequest()()
            println("Top200: Found ${page.photos.size} photos, hasNext=${page.hasNext}")
            page.photos.take(3).forEach { photo ->
                println("ID: ${photo.id}, Title: ${photo.title}")
            }
        }
    }

    @Test
    fun `test top favorites photos request`() {
        runTest {
            val page = TopFavoritesPhotosRequest()()
            println("TopFavorites: Found ${page.photos.size} photos, hasNext=${page.hasNext}")
            page.photos.take(3).forEach { photo ->
                println("ID: ${photo.id}, Title: ${photo.title}")
            }
        }
    }

    @Test
    fun `test top applicants photos request`() {
        runTest {
            val page = TopApplicantsPhotosRequest()()
            println("TopApplicants: Found ${page.photos.size} photos, hasNext=${page.hasNext}")
            page.photos.take(3).forEach { photo ->
                println("ID: ${photo.id}, Title: ${photo.title}")
            }
        }
    }
}
