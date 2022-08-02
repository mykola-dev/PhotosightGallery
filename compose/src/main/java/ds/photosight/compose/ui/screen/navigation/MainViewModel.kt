package ds.photosight.compose.ui.screen.navigation

import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ds.photosight.compose.data.PAGE_SIZE
import ds.photosight.compose.data.PhotosPagingSource
import ds.photosight.compose.ui.BaseViewModel
import ds.photosight.compose.ui.model.MenuState
import ds.photosight.parser.PhotoInfo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    log: Timber.Tree
) : BaseViewModel(log) {

    private lateinit var menu: Flow<MenuState>
    private val _photosPagedLiveData = MutableStateFlow<PagingData<PhotoInfo>>(PagingData.empty())
    val photosPagedFlow: Flow<PagingData<PhotoInfo>> = _photosPagedLiveData

    fun setMenuStateFlow(menuState: Flow<MenuState>) {
        if (!::menu.isInitialized) {
            menu = menuState
            launch {
                menuState
                    .flatMapLatest { providePhotosStream(it) }
                    .collect {
                        _photosPagedLiveData.value = it
                    }
            }
        }
    }

    private fun providePhotosStream(menuState: MenuState): Flow<PagingData<PhotoInfo>> = flow {
        emit(PagingData.empty())    // cleanup list first
        emitAll(
            Pager(
                config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PAGE_SIZE / 2, enablePlaceholders = false),
                pagingSourceFactory = {
                    log.d("instantiating photos paging source")
                    PhotosPagingSource(menuState)
                }
            )
                .flow
                .cachedIn(viewModelScope)
        )
    }

}