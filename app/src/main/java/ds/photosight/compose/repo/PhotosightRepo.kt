package ds.photosight.compose.repo

import android.annotation.SuppressLint
import android.content.Context
import ds.photosight.compose.ui.screen.gallery.RatingMenuItemState
import ds.photosight.parser.CategoriesRequest
import ds.photosight.parser.PhotoCategory
import ds.photosight.parser.PhotoDetails
import ds.photosight.parser.PhotoDetailsRequest
import ds.photosight.parser.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotosightRepo(private val context: Context) {

    suspend fun <T> apiRequest(request: Request<T>): T = withContext(Dispatchers.Default) {
        println("==> executing request ${request.url}")
        val result = request()
        println("<== end")
        result
    }

    /**
     * @return sorted and localized categories list
     */
    suspend fun getCategories(): List<PhotoCategory> = apiRequest(CategoriesRequest())
        .sortedWith(categoriesSorter)
        .map { it.copy(name = it.getLocalizedCategory(context)) }


    suspend fun getPhotoDetails(photoId: Int): PhotoDetails = apiRequest(PhotoDetailsRequest(photoId))

    fun getRatingsList(): List<RatingMenuItemState> = RatingMenuItemState.Type
        .entries
        .map {
            RatingMenuItemState(
                type = it,
                title = context.getString(it.resId),
            )
        }

}

@SuppressLint("DiscouragedApi")
private fun PhotoCategory.getLocalizedCategory(ctx: Context): String = ctx.resources.run {
    getIdentifier("category_$index", "string", ctx.packageName)
        .takeIf { it != 0 }
        ?.let { getString(it) }
        ?: name
}

private val categoriesSorter = compareBy<PhotoCategory> { it.index != 15 }