package ds.photosight.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import dagger.hilt.android.qualifiers.ApplicationContext
import ds.photosight.R
import ds.photosight.parser.*
import ds.photosight.viewmodel.MenuItemState
import ds.photosight.viewmodel.MenuState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotosightRepo @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun <T> apiRequest(request: Request<T>): T = withContext(Dispatchers.Default) {
        request.invoke()
    }

    /**
     * @return sorted and localized categories list
     */
    suspend fun getCategories(): List<PhotoCategory> = apiRequest(CategoriesRequest())
        .sortedWith(categoriesSorter)
        .map { it.copy(name = it.getLocalizedCategory(context)) }

    fun getRatingsList(): List<MenuItemState> = context
        .resources
        .getStringArray(R.array.ratings_array)
        .mapIndexed { idx, title -> MenuItemState(MenuState.MENU_RATINGS, idx, title, false) }


}

private fun PhotoCategory.getLocalizedCategory(ctx: Context): String = ctx.resources.run {
    getIdentifier("category_$index", "string", ctx.packageName)
        .takeIf { it != 0 }
        ?.let { getString(it) }
        ?: name
}

private val categoriesSorter = compareBy<PhotoCategory> { it.index != 15 }