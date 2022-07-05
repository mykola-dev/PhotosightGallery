package ds.photosight.ui.viewmodel

import androidx.lifecycle.*
import androidx.paging.*
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import ds.photosight.core.Prefs
import ds.photosight.parser.PhotoInfo
import ds.photosight.repo.PAGE_SIZE
import ds.photosight.repo.PhotosPagingSource
import ds.photosight.utils.invoke
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    override val prefs: Prefs,
    override val log: Timber.Tree
) : BaseViewModel() {

    private lateinit var menu: LiveData<MenuState>
    private val _photosPagedLiveData = MediatorLiveData<PagingData<PhotoInfo>>()
    val photosPagedLiveData: LiveData<PagingData<PhotoInfo>> = _photosPagedLiveData

    val transitionEndListener = LiveEvent<Unit>()

    fun setMenuStateLiveData(menuState: LiveData<MenuState>) {
        if (!::menu.isInitialized) {
            menu = menuState
            _photosPagedLiveData.addSource(menuState.switchMap { providePhotosStream(it) }) { data ->
                log.v("photo data received")
                _photosPagedLiveData.value = data
            }
        }
    }

    private fun providePhotosStream(menuState: MenuState): LiveData<PagingData<PhotoInfo>> = liveData {
        emit(PagingData.empty())    // cleanup list first
        emitSource(
            Pager(
                config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PAGE_SIZE / 2, enablePlaceholders = false),
                pagingSourceFactory = {
                    log.d("instantiating photos paging source")
                    PhotosPagingSource(menuState)
                }
            )
                .liveData
                .cachedIn(viewModelScope)
        )
    }

    fun onTransitionEnd() {
        transitionEndListener()
    }
}