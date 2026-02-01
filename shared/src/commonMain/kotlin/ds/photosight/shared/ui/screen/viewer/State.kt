package ds.photosight.shared.ui.screen.viewer

import ds.photosight.shared.ui.model.Photo
import ds.photosight.parser.PhotoDetails

data class ViewerState(
    val showUi: Boolean = false,
    val currentPhoto: Photo? = null,
    val title: String = "",
    val subtitle: String = "",
    val details: DetailsState = DetailsState.Hidden,
)

sealed interface DetailsState {
    data object Loading : DetailsState
    data object Error : DetailsState
    data object Hidden : DetailsState
    data class Payload(val details: PhotoDetails) : DetailsState
}
