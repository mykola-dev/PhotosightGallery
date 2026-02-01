package ds.photosight.shared.usecase

import ds.photosight.shared.ui.screen.gallery.MenuState

expect class ToolbarDataUseCase() {
    suspend fun getTitle(menuState: MenuState? = null): String
    fun getSubtitle(photoInfo: ds.photosight.shared.ui.model.Photo? = null): String?
}
