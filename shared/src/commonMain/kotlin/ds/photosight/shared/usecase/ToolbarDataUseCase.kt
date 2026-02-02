package ds.photosight.shared.usecase

import ds.photosight.shared.ui.screen.gallery.CategoryMenuItemState
import ds.photosight.shared.ui.screen.gallery.MenuState
import ds.photosight.shared.ui.screen.gallery.RatingMenuItemState
import org.jetbrains.compose.resources.getString

class ToolbarDataUseCase(
    private val appNameProvider: AppNameUseCase
) {
    suspend fun getTitle(menuState: MenuState?): String {
        val item = menuState?.selectedItem ?: return appNameProvider()
        val resource = when (item) {
            is CategoryMenuItemState -> item.category.titleRes
            is RatingMenuItemState -> item.type.titleRes
        }
        return getString(resource)
    }

    fun getSubtitle(photoInfo: ds.photosight.shared.ui.model.Photo? = null): String? = photoInfo
        ?.paginationKey
        ?.let { key ->
            if ("/" in key) key
            else "Page $key"
        }
}
