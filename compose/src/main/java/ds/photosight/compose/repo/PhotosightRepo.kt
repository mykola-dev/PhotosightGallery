package ds.photosight.compose.repo

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ds.photosight.compose.ui.screen.gallery.RatingMenuItemState
import ds.photosight.parser.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotosightRepo @Inject constructor(@ApplicationContext private val context: Context) {

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
        .values()
        .map {
            RatingMenuItemState(
                type = it,
                title = context.getString(it.resId),
            )
        }

}

private fun PhotoCategory.getLocalizedCategory(ctx: Context): String = ctx.resources.run {
    getIdentifier("category_$index", "string", ctx.packageName)
        .takeIf { it != 0 }
        ?.let { getString(it) }
        ?: name
}

private val categoriesSorter = compareBy<PhotoCategory> { it.index != 15 }