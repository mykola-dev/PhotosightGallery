package ds.photosight.shared.ui.screen.viewer

import ds.photosight.shared.repo.PhotosightRepo
import ds.photosight.shared.ui.BaseViewModel
import ds.photosight.shared.ui.events.UiEvent
import ds.photosight.shared.ui.model.Photo
import ds.photosight.shared.usecase.DownloadUseCase
import ds.photosight.shared.usecase.OpenBrowserUseCase
import ds.photosight.shared.usecase.ShareUseCase
import ds.photosight.parser.PhotoDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewerViewModel(
    private val shareUseCase: ShareUseCase,
    private val downloadUseCase: DownloadUseCase,
    private val openBrowserUseCase: OpenBrowserUseCase,
    private val repo: PhotosightRepo,
) : BaseViewModel() {

    private val _state = MutableStateFlow(ViewerState())
    val state: StateFlow<ViewerState>
        get() = _state.asStateFlow()

    val photo: Photo
        get() = _state.value.currentPhoto ?: error("must be not null")

    private val detailsCache = mutableMapOf<Int, PhotoDetails>()

    private fun fetchDetails() = launch {
        val detailsState =
            try {
                val details: PhotoDetails =
                    detailsCache.getOrPut(photo.id) { repo.getPhotoDetails(photo.id) }
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
            )
        }
    }

    fun onUrlShare() {
        shareUseCase.shareUrl(photo.pageUrl)
    }

    fun onImageShare() = launch {
        shareUseCase.shareImage(photo.large)
    }

    fun onDrawerStateChanged(isOpen: Boolean) {
        if (isOpen) {
            _state.update { it.copy(showUi = false, details = DetailsState.Loading) }
            fetchDetails()
        } else {
            _state.update { it.copy(details = DetailsState.Hidden) }
        }
    }

    fun onOpenBrowser() {
        openBrowserUseCase(photo.pageUrl)
    }

    fun onInfo() {
        event(UiEvent.OpenInfo())
    }

    fun saveFile() = launch {
        downloadUseCase(photo)
        event(UiEvent.Snack("saved_successfully"))
    }

    fun providePhotoTitle(): String = photo.title + ".jpg"
}
