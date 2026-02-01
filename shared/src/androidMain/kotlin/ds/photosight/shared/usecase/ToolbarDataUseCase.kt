package ds.photosight.shared.usecase

import ds.photosight.shared.ui.screen.gallery.CategoryMenuItemState
import ds.photosight.shared.ui.screen.gallery.MenuState
import ds.photosight.shared.ui.screen.gallery.RatingMenuItemState
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class ToolbarDataUseCase : KoinComponent {
    private val appNameProvider: AppNameUseCase by inject()

    actual suspend fun getTitle(menuState: MenuState?): String {
         val item = menuState?.selectedItem ?: return appNameProvider()
         val resource = when (item) {
             is CategoryMenuItemState -> item.category.titleRes
             is RatingMenuItemState -> item.type.titleRes
         }
         return getString(resource)
    }

    actual fun getSubtitle(photoInfo: ds.photosight.shared.ui.model.Photo?): String? = photoInfo
        ?.paginationKey
        ?.let { key ->
            if ("/" in key) key
            else "Page $key"
        }
}
