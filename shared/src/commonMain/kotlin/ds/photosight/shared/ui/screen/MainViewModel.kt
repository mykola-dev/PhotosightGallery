package ds.photosight.shared.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import ds.photosight.shared.repo.PAGE_SIZE
import ds.photosight.shared.repo.PhotosPagingSourceFactory
import ds.photosight.shared.ui.model.Photo
import ds.photosight.shared.ui.screen.gallery.MenuState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import io.github.aakira.napier.Napier

class MainViewModel(
    private val photosPagingSourceFactory: PhotosPagingSourceFactory,
) : ViewModel() {

    private lateinit var menu: Flow<MenuState>
    private val _photosPagedFlow = MutableStateFlow<PagingData<Photo>>(PagingData.empty())
    val photosPagedFlow: StateFlow<PagingData<Photo>> get() = _photosPagedFlow

    var selectedId: Int = 0
        private set

    fun setMenuStateFlow(menuState: Flow<MenuState>) {
        if (!::menu.isInitialized) {
            menu = menuState
            viewModelScope.launch {
                menuState
                    .flatMapLatest { providePhotosStream(it) }
                    .collect { _photosPagedFlow.value = it }
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
                    Napier.d("instantiating photos paging source")
                    photosPagingSourceFactory(menuState)
                }
            )
                .flow
                .map { it }
                .cachedIn(viewModelScope)
        )
    }

    fun onPhotoSelected(id: Int) {
        selectedId = id
    }
}
