@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package ds.photosight.shared.ui.screen.gallery

import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Immutable
import ds.photosight.parser.CategoriesPhotosRequest
import ds.photosight.shared.ui.model.Category
import org.jetbrains.compose.resources.StringResource
import photosight.shared.generated.resources.Res
import photosight.shared.generated.resources.new_photos
import photosight.shared.generated.resources.rating_applicants
import photosight.shared.generated.resources.rating_day
import photosight.shared.generated.resources.rating_favorites
import photosight.shared.generated.resources.rating_month
import photosight.shared.generated.resources.rating_week

data class GalleryState(
        val title: String,
        val subtitle: String? = null,
        val isLoading: Boolean = true,
        val showAboutDialog: Boolean = false
)

data class PhotosFilter(
        val filterDumpCategory: CategoriesPhotosRequest.FilterDumpCategory =
                CategoriesPhotosRequest.FilterDumpCategory.ALL,
        val sortTypeCategory: CategoriesPhotosRequest.SortTypeCategory =
                CategoriesPhotosRequest.SortTypeCategory.DEFAULT,
)

@Immutable
data class MenuState(
        val categories: List<CategoryMenuItemState> = emptyList(),
        val ratings: List<RatingMenuItemState> = emptyList(),
        val selectedItem: MenuItemState? = ratings.firstOrNull(), // default is 'new photos'
        val categoriesFilter: PhotosFilter? = null,
        val bottomSheetState: SheetValue = SheetValue.PartiallyExpanded
)

enum class MenuTabs(val resId: String) {
    RATINGS("ratings"),
    CATEGORIES("categories")
}

sealed interface MenuItemState

data class CategoryMenuItemState(
        val category: Category,
) : MenuItemState

data class RatingMenuItemState(
        val type: Type,
) : MenuItemState {

    enum class Type(val resId: String, val titleRes: StringResource) {
        ALL("new_photos", Res.string.new_photos),
        DAY("rating_day", Res.string.rating_day),
        WEEK("rating_week", Res.string.rating_week),
        MONTH("rating_month", Res.string.rating_month),
        FAVS("rating_favorites", Res.string.rating_favorites),
        APPLICANTS("rating_applicants", Res.string.rating_applicants),
    }
}

