package ds.photosight.compose.ui.screen.viewer

import dagger.hilt.android.lifecycle.HiltViewModel
import ds.photosight.compose.ui.BaseViewModel
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.usecase.OpenBrowserUseCase
import ds.photosight.compose.usecase.ShareUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ViewerViewModel @Inject constructor(
    private val shareUseCase: ShareUseCase,
    private val openBrowserUseCase: OpenBrowserUseCase,
    log: Timber.Tree,
) : BaseViewModel(log) {

    private val _state = MutableStateFlow(ViewerState())
    val state: StateFlow<ViewerState> get() = _state.asStateFlow()

    val photo: Photo get() = _state.value.currentPhoto ?: error("must be not null")

    fun onClicked() {
        _state.update { it.copy(showUi = !it.showUi) }
    }

    fun onPageChanged(item: Photo) {
        _state.update {
            it.copy(
                currentPhoto = item,
                title = item.title,
                subtitle = item.authorName
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

    }

    fun onDownload() {

    }

    fun onOpenBrowser() {
        openBrowserUseCase(photo.pageUrl)
    }

    fun onInfo() {

    }


}
