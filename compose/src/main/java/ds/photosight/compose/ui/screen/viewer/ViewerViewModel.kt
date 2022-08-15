package ds.photosight.compose.ui.screen.viewer

import androidx.compose.material.DrawerValue
import dagger.hilt.android.lifecycle.HiltViewModel
import ds.photosight.compose.repo.PhotosightRepo
import ds.photosight.compose.ui.BaseViewModel
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.usecase.OpenBrowserUseCase
import ds.photosight.compose.usecase.ShareUseCase
import ds.photosight.parser.PhotoDetails
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

    private val detailsCache = mutableMapOf<Int, PhotoDetails>()

    //private val detailsMutableSharedFlow = MutableSharedFlow<PhotoDetails>(Int.MAX_VALUE, Int.MAX_VALUE)
    /*private val detailsFlow: SharedFlow<PhotoDetails> = flow {
        val result = repo.getPhotoDetails(photo.id)
        emit(result)
        log.v("fetched")
    }
        .shareIn(viewModelScope, SharingStarted.Lazily, Int.MAX_VALUE)

    init {
        detailsFlow.replayCache
    }
*/
    private fun fetchDetails() = launch {
        log.v("fetching...")
        val detailsState = try {
            val details: PhotoDetails = detailsCache.getOrPut(photo.id) { repo.getPhotoDetails(photo.id) }
            log.v("publishing")
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

    fun onDrawerStateChanged(value: DrawerValue) {
        when (value) {
            DrawerValue.Open -> {
                _state.update {
                    it.copy(showUi = false)
                }
                fetchDetails()
            }
            DrawerValue.Closed -> {
                _state.update {
                    it.copy(details = DetailsState.Loading)
                }
            }
        }
    }

    fun onDownload() {

    }

    fun onOpenBrowser() {
        openBrowserUseCase(photo.pageUrl)
    }

    fun onInfo() {

    }


}
