package ds.photosight.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import ds.photosight.core.Prefs
import ds.photosight.parser.PhotoDetails
import ds.photosight.repo.PhotosightRepo
import ds.photosight.utils.position
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ViewerViewModel @Inject constructor(
    override val prefs: Prefs,
    override val log: Timber.Tree,
    private val photosightRepo: PhotosightRepo,
    val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    val position: Int get() = savedStateHandle.position!!
    private val photoDetailsMap: MutableMap<Int, PhotoDetails> = mutableMapOf()

    init {
        log.v("saved state: ${savedStateHandle.keys()}")
    }

    private val _commentsState: MutableLiveData<CommentsState> = MutableLiveData(CommentsState.Loading)
    val commentsState: LiveData<CommentsState> = _commentsState

    fun loadComments(photoId: Int) = launch {
        try {
            val details = photoDetailsMap.getOrPut(photoId) { photosightRepo.getPhotoDetails(photoId) }
            _commentsState.value = CommentsState.Payload(details)
        } catch (e: Exception) {
            _commentsState.value = CommentsState.Error
        }
    }

    fun onDrawerClosed() {
        _commentsState.value = CommentsState.Loading
    }

}

