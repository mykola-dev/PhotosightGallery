package ds.photosight.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*
import ds.photosight.core.Prefs
import ds.photosight.parser.PhotoInfo
import ds.photosight.repo.PhotosPagingSource
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(
    override val prefs: Prefs,
    override val log: Timber.Tree
) : BaseViewModel() {

    private lateinit var menu: LiveData<MenuState>
    private val _photosPagedLiveData = MediatorLiveData<PagingData<PhotoInfo>>()
    val photosPagedLiveData: LiveData<PagingData<PhotoInfo>> = _photosPagedLiveData

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
                config = PagingConfig(pageSize = 24, prefetchDistance = 12, enablePlaceholders = false),
                pagingSourceFactory = {
                    log.d("instantiating photos paging source")
                    PhotosPagingSource(menuState)
                }
            )
                .liveData
                .cachedIn(viewModelScope)
        )
    }
}