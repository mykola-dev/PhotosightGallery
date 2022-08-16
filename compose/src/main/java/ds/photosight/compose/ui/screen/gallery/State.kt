package ds.photosight.compose.ui.screen.gallery

import androidx.annotation.StringRes
import androidx.compose.material.BottomSheetValue
import androidx.compose.runtime.Immutable
import ds.photosight.compose.R
import ds.photosight.parser.CategoriesPhotosRequest

data class GalleryState(
    val title: String,
    val subtitle: String? = null,
    val isLoading: Boolean = true,
    val showAboutDialog: Boolean = false
)

data class PhotosFilter(
    val filterDumpCategory: CategoriesPhotosRequest.FilterDumpCategory = CategoriesPhotosRequest.FilterDumpCategory.ALL,
    val sortTypeCategory: CategoriesPhotosRequest.SortTypeCategory = CategoriesPhotosRequest.SortTypeCategory.DEFAULT,
)

@Immutable
data class MenuState(
    val categories: List<CategoryMenuItemState> = emptyList(),
    val ratings: List<RatingMenuItemState> = emptyList(),
    val selectedItem: MenuItemState? = ratings.firstOrNull(),    // default is 'new photos'
    val categoriesFilter: PhotosFilter? = null,
    val bottomSheetState: BottomSheetValue = BottomSheetValue.Collapsed
)

enum class MenuTabs(@StringRes val resId: Int) {
    RATINGS(R.string.ratings),
    CATEGORIES(R.string.categories)
}

sealed interface MenuItemState {
    val title: String
}

data class CategoryMenuItemState(
    val category: Int,
    override val title: String,
) : MenuItemState

data class RatingMenuItemState(
    val type: Type,
    override val title: String,
) : MenuItemState {


    enum class Type(@StringRes val resId: Int) {
        ALL(R.string.new_photos),
        DAY(R.string.rating_day),
        WEEK(R.string.rating_week),
        MONTH(R.string.rating_month),
        FAVS(R.string.rating_favorites),
        APPLICANTS(R.string.rating_applicants),
    }
}