package ds.photosight.compose.ui.screen.gallery

data class GalleryState(
    val title: String,
    val subtitle: String? = null,
    val isLoading: Boolean = true,
    //val firstVisibleItem: Int = 0,
    val showAboutDialog: Boolean = false
)