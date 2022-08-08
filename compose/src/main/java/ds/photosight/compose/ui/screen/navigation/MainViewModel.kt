package ds.photosight.compose.ui.screen.navigation

import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ds.photosight.compose.data.asUiModel
import ds.photosight.compose.repo.PAGE_SIZE
import ds.photosight.compose.repo.PhotosPagingSourceFactory
import ds.photosight.compose.ui.BaseViewModel
import ds.photosight.compose.ui.model.MenuState
import ds.photosight.compose.ui.model.Photo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    log: Timber.Tree,
    private val photosPagingSourceFactory: PhotosPagingSourceFactory,
) : BaseViewModel(log) {

    private lateinit var menu: Flow<MenuState>
    private val _photosPagedLiveData = MutableStateFlow<PagingData<Photo>>(PagingData.empty())
    val photosPagedFlow: StateFlow<PagingData<Photo>> get() = _photosPagedLiveData

    var selectedPhoto: Int? = 0
        private set

    fun setMenuStateFlow(menuState: Flow<MenuState>) {
        if (!::menu.isInitialized) {
            menu = menuState
            launch {
                menuState
                    .flatMapLatest { providePhotosStream(it) }
                    .collect { _photosPagedLiveData.value = it }
            }
        }
    }

    private fun providePhotosStream(menuState: MenuState): Flow<PagingData<Photo>> = flow {
        emit(PagingData.empty())    // cleanup list first
        if (menuState.selectedItem == null) return@flow
        delay(100) // this is required for triggering the empty data on ui (???)
        emitAll(
            Pager(
                config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PAGE_SIZE / 2, enablePlaceholders = false),
                pagingSourceFactory = {
                    log.d("instantiating photos paging source")
                    photosPagingSourceFactory(menuState)
                }
            )
                .flow
                .map { it }
                .cachedIn(viewModelScope)
        )
    }

    fun onPhotoSelected(index: Int) {
        selectedPhoto = index
    }

}