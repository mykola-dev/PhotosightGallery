package ds.photosight.compose.ui.screen.viewer

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ds.photosight.compose.ui.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ViewerViewModel @Inject constructor(
    state: SavedStateHandle,
    log: Timber.Tree
) : BaseViewModel(log) {
    //val currentPage=
}

data class ViewerArgs(
    val currentPage: Int
)