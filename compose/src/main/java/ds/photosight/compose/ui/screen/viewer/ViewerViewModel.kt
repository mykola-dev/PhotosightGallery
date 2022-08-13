package ds.photosight.compose.ui.screen.viewer

import dagger.hilt.android.lifecycle.HiltViewModel
import ds.photosight.compose.repo.PhotosightRepo
import ds.photosight.compose.ui.BaseViewModel
import ds.photosight.compose.ui.events.UiEvent
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.usecase.OpenBrowserUseCase
import ds.photosight.compose.usecase.ShareUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ViewerViewModel @Inject constructor(
    private val shareUseCase: ShareUseCase,
    private val openBrowserUseCase: OpenBrowserUseCase,
    private val repo: PhotosightRepo,
    log: Timber.Tree,
) : BaseViewModel(log) {

    private val _state = MutableStateFlow(ViewerState())
    val state: StateFlow<ViewerState> get() = _state.asStateFlow()

    val photo: Photo get() = _state.value.currentPhoto ?: error("must be not null")

    fun fetchDetails() = launch {
        val detailsState = try {
            val details = repo.getPhotoDetails(photo.id)
            DetailsState.Payload(details)
        } catch (e: Exception) {
            e.printStackTrace()
            DetailsState.Error
        }
        _state.update { it.copy(details = detailsState) }
    }

    fun onClicked() {
        _state.update { it.copy(showUi = !it.showUi) }
    }

    fun onPageChanged(item: Photo) {
        _state.update {
            it.copy(
                currentPhoto = item,
                title = item.title,
                subtitle = item.authorName,
                details = DetailsState.Loading
            )
        }
    }

    fun onUrlShare() {
        shareUseCase.shareUrl(photo.pageUrl)
    }

    fun onImageShare() {
        shareUseCase.shareImage(photo.large)
    }

    fun onDrawerToggle() {
        event(UiEvent.OpenDrawer())
        _state.update { it.copy(showUi = false) }
        fetchDetails()
    }

    fun onDownload() {

    }

    fun onOpenBrowser() {
        openBrowserUseCase(photo.pageUrl)
    }

    fun onInfo() {

    }


}
