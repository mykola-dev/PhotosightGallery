package ds.photosight.compose.ui.screen.viewer

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ds.photosight.compose.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ViewerViewModel @Inject constructor(
    //state: SavedStateHandle,
    log: Timber.Tree
) : BaseViewModel(log) {

    private val _state = MutableStateFlow(ViewerState())
    val state: StateFlow<ViewerState> get() = _state.asStateFlow()


}

data class ViewerArgs(
    val currentPage: Int
)