package ds.photosight.ui.viewmodel

import android.content.Context
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import ds.photosight.R
import ds.photosight.parser.CategoriesPhotosRequest
import ds.photosight.parser.PhotoDetails

interface MenuItemState {
    val title: String
    var isSelected: Boolean
}

data class CategoryMenuItemState(
    val category: Int,
    override val title: String,
    override var isSelected: Boolean = false
) : MenuItemState

sealed class RatingMenuItemState(@StringRes val titleId: Int) : MenuItemState {

    abstract val context: Context
    override val title: String get() = context.getString(titleId)
    override var isSelected: Boolean = false

    data class Day(override val context: Context) : RatingMenuItemState(R.string.rating_day)
    data class Week(override val context: Context) : RatingMenuItemState(R.string.rating_week)
    data class Month(override val context: Context) : RatingMenuItemState(R.string.rating_month)
    //data class Art(override val context: Context) : RatingMenuItemState(R.string.rating_artistic)
    data class Orig(override val context: Context) : RatingMenuItemState(R.string.rating_original)
    data class Tech(override val context: Context) : RatingMenuItemState(R.string.rating_technic)
    data class Favs(override val context: Context) : RatingMenuItemState(R.string.rating_favorites)
    data class Applicants(override val context: Context) : RatingMenuItemState(R.string.rating_applicants)
    //data class Outrun(override val context: Context) : RatingMenuItemState(R.string.rating_outrun)
    data class All(override val context: Context) : RatingMenuItemState(R.string.new_photos)

}

data class MenuState(
    val categories: List<CategoryMenuItemState>,
    val ratings: List<RatingMenuItemState>,
    val categoriesFilter: PhotosFilter.Categories = PhotosFilter.Categories()
) {
    fun getSelected(): MenuItemState = (categories + ratings).first { it.isSelected }

    @IntDef(MENU_RATINGS, MENU_CATEGORIES)
    annotation class MenuStatePosition

    companion object {
        const val MENU_RATINGS = 0
        const val MENU_CATEGORIES = 1
    }

}

interface PhotosFilter {
    var enabled: Boolean

    data class Categories(
        val filterDumpCategory: CategoriesPhotosRequest.FilterDumpCategory = CategoriesPhotosRequest.FilterDumpCategory.ALL,
        val sortTypeCategory: CategoriesPhotosRequest.SortTypeCategory = CategoriesPhotosRequest.SortTypeCategory.DEFAULT,
        override var enabled: Boolean = false
    ) : PhotosFilter

}

sealed class CommentsState(val error: Boolean, val loading: Boolean) {
    object Loading : CommentsState(false, true)
    object Error : CommentsState(true, false)
    class Payload(val details: PhotoDetails) : CommentsState(false, false)
}

fun MutableLiveData<MenuState>.reduce(item: MenuItemState) = with(value!!) {
    value = copy(
        categories = categories.onEach { it.isSelected = item is CategoryMenuItemState && item.category == it.category },
        ratings = ratings.onEach { it.isSelected = item.javaClass == it.javaClass },
        categoriesFilter = categoriesFilter.copy(enabled = item is CategoryMenuItemState)
    )
}

fun MutableLiveData<MenuState>.reduce(filter: PhotosFilter) = with(value!!) {
    value = when (filter) {
        is PhotosFilter.Categories -> copy(categoriesFilter = filter)
        else -> error("not supported")
    }
}