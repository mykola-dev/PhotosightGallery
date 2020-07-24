package ds.photosight.repo

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ds.photosight.R
import ds.photosight.parser.*
import ds.photosight.ui.viewmodel.RatingMenuItemState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotosightRepo @Inject constructor(@ApplicationContext private val context: Context) {

    private suspend fun <T> apiRequest(request: Request<T>): T = withContext(Dispatchers.Default) {
        request()
    }

    /**
     * @return sorted and localized categories list
     */
    suspend fun getCategories(): List<PhotoCategory> = apiRequest(CategoriesRequest())
        .sortedWith(categoriesSorter)
        .map { it.copy(name = it.getLocalizedCategory(context)) }

    fun getRatingsList(): List<RatingMenuItemState> = listOf(
        RatingMenuItemState::All,
        RatingMenuItemState::Day,
        RatingMenuItemState::Week,
        RatingMenuItemState::Month,
        RatingMenuItemState::Art,
        RatingMenuItemState::Orig,
        RatingMenuItemState::Tech,
        RatingMenuItemState::Favs,
        RatingMenuItemState::Applicants,
        RatingMenuItemState::Outrun
    )
        .map { it(context) }

    suspend fun getPhotoDetails(photoId: Int): PhotoDetails = apiRequest(PhotoDetailsRequest(photoId))


}

private fun PhotoCategory.getLocalizedCategory(ctx: Context): String = ctx.resources.run {
    getIdentifier("category_$index", "string", ctx.packageName)
        .takeIf { it != 0 }
        ?.let { getString(it) }
        ?: name
}

private val categoriesSorter = compareBy<PhotoCategory> { it.index != 15 }