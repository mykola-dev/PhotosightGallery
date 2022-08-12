package ds.photosight.compose.ui.screen.viewer

import ds.photosight.compose.ui.model.Photo

data class ViewerState(
    val showUi: Boolean = false,
    val currentPhoto: Photo? = null,
    val title: String = "",
    val subtitle: String = "",
) {
}