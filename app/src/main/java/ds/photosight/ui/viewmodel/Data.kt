package ds.photosight.ui.viewmodel

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import ds.photosight.R
import ds.photosight.parser.BestPhotosRequest
import ds.photosight.parser.CategoriesPhotosRequest

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
    data class Art(override val context: Context) : RatingMenuItemState(R.string.rating_artistic)
    data class Orig(override val context: Context) : RatingMenuItemState(R.string.rating_original)
    data class Tech(override val context: Context) : RatingMenuItemState(R.string.rating_technic)
    data class Favs(override val context: Context) : RatingMenuItemState(R.string.rating_favorites)
    data class Applicants(override val context: Context) : RatingMenuItemState(R.string.rating_applicants)
    data class Outrun(override val context: Context) : RatingMenuItemState(R.string.rating_outrun)
    data class All(override val context: Context) : RatingMenuItemState(R.string.all_photos)

}

data class MenuState(
    val categories: List<CategoryMenuItemState>,
    val ratings: List<RatingMenuItemState>,
    val categoriesFilter: PhotosFilter.Categories = PhotosFilter.Categories()
) {
    fun getSelected(): MenuItemState = (categories + ratings).first { it.isSelected }

    companion object {
        const val MENU_RATINGS = 0
        const val MENU_CATEGORIES = 1
    }

}

interface PhotosFilter {
    var enabled: Boolean

    data class Categories(
        val sortDumpCategory: CategoriesPhotosRequest.SortDumpCategory = CategoriesPhotosRequest.SortDumpCategory.ALL,
        val sortTypeCategory: CategoriesPhotosRequest.SortTypeCategory = CategoriesPhotosRequest.SortTypeCategory.DEFAULT,
        override var enabled: Boolean = false
    ) : PhotosFilter

    data class BestPhotos(
        val sort: BestPhotosRequest.Sort = BestPhotosRequest.Sort.BEST,
        val time: BestPhotosRequest.Time = BestPhotosRequest.Time.DAY,
        override var enabled: Boolean
    ) : PhotosFilter

    data class DailyPhotos(
        val category: Int? = null,
        override var enabled: Boolean = false
    ) : PhotosFilter

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