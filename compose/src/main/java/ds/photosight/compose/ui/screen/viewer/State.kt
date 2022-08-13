package ds.photosight.compose.ui.screen.viewer

import ds.photosight.compose.ui.model.Photo
import ds.photosight.parser.PhotoDetails

data class ViewerState(
    val showUi: Boolean = false,
    val currentPhoto: Photo? = null,
    val title: String = "",
    val subtitle: String = "",
    val details: DetailsState = DetailsState.Loading,
)

sealed interface DetailsState {
    object Loading : DetailsState
    object Error : DetailsState
    class Payload(val details: PhotoDetails) : DetailsState
}

