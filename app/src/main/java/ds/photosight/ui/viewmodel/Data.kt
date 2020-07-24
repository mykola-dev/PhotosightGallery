package ds.photosight.ui.viewmodel

import android.content.Context
import androidx.annotation.StringRes
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
    val categoriesFilter: CategoriesFilter = CategoriesFilter(true)
) {
    fun getSelected(): MenuItemState = (categories + ratings).first { it.isSelected }

    companion object {
        const val MENU_RATINGS = 0
        const val MENU_CATEGORIES = 1
    }

}

interface PhotosFilter {
    val enabled: Boolean
}

data class CategoriesFilter(
    override val enabled: Boolean,
    val sortDumpCategory: CategoriesPhotosRequest.SortDumpCategory = CategoriesPhotosRequest.SortDumpCategory.ALL,
    val sortTypeCategory: CategoriesPhotosRequest.SortTypeCategory = CategoriesPhotosRequest.SortTypeCategory.DEFAULT
) : PhotosFilter

data class BestPhotosFilter(
    override val enabled: Boolean,
    val sort: BestPhotosRequest.Sort = BestPhotosRequest.Sort.BEST,
    val time: BestPhotosRequest.Time = BestPhotosRequest.Time.DAY
) : PhotosFilter

data class DailyPhotosFilter(
    override val enabled: Boolean,
    val category: Int? = null
) : PhotosFilter


