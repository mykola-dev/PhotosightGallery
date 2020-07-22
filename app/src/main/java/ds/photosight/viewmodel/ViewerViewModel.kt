package ds.photosight.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import ds.photosight.core.Prefs
import ds.photosight.parser.PhotoDetails
import ds.photosight.repo.PhotosightRepo
import kotlinx.coroutines.launch
import timber.log.Timber

class ViewerViewModel @ViewModelInject constructor(
    override val prefs: Prefs,
    override val log: Timber.Tree,
    private val photosightRepo: PhotosightRepo,
    @Assisted val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val position: Int get() = savedStateHandle.get<Int>("position")!!

    init {
        log.v("saved state: ${savedStateHandle.keys()}")
    }

    private val _commentsState: MutableLiveData<CommentsState> = MutableLiveData(CommentsState.Loading)
    val commentsState: LiveData<CommentsState> = _commentsState

    fun loadComments(photoId: Int) = launch {
        val details = photosightRepo.getPhotoDetails(photoId)
        _commentsState.value = CommentsState.Payload(details)
    }

    fun onDrawerClosed() {
        _commentsState.value = CommentsState.Loading
    }

}

sealed class CommentsState(val error: Boolean, val loading: Boolean) {
    object Loading : CommentsState(false, true)
    object Error : CommentsState(true, false)
    class Payload(val details: PhotoDetails) : CommentsState(false, false)
}
