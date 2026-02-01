package ds.photosight.shared.repo

import ds.photosight.parser.CategoriesRequest
import ds.photosight.parser.PhotoCategory
import ds.photosight.parser.PhotoDetails
import ds.photosight.parser.PhotoDetailsRequest
import ds.photosight.parser.Request
import ds.photosight.shared.ui.screen.gallery.RatingMenuItemState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotosightRepo {

    suspend fun <T> apiRequest(request: Request<T>): T = withContext(Dispatchers.Default) {
        println("==> executing request ${request.url}")
        val result = request()
        println("<== end")
        result
    }

    /**
     * @return sorted categories list
     */
    suspend fun getCategories(): List<PhotoCategory> = apiRequest(CategoriesRequest())
        .sortedWith(categoriesSorter)

    suspend fun getPhotoDetails(photoId: Int): PhotoDetails = apiRequest(PhotoDetailsRequest(photoId))

    fun getRatingsList(): List<RatingMenuItemState> = RatingMenuItemState.Type
        .entries
        .map { type ->
            RatingMenuItemState(
                type = type,
            )
        }
}

private val categoriesSorter = compareBy<PhotoCategory> { it.index != 15 }
