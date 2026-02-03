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
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val photosPagingSourceFactory: PhotosPagingSourceFactory,
) : ViewModel() {

    private lateinit var menu: Flow<MenuState>
    private val _photosPagedFlow = MutableStateFlow<PagingData<Photo>>(PagingData.empty())
    val photosPagedFlow: StateFlow<PagingData<Photo>> get() = _photosPagedFlow

    var selectedId: Int = 0
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    fun setMenuStateFlow(menuState: Flow<MenuState>) {
        if (!::menu.isInitialized) {
            menu = menuState
            viewModelScope.launch {
                menuState
                    .distinctUntilChanged { old, new ->
                        old.selectedItem == new.selectedItem && old.categoriesFilter == new.categoriesFilter
                    }
                    .flatMapLatest { providePhotosStream(it) }
                    .collect { _photosPagedFlow.value = it }
            }
        }
    }

    private fun providePhotosStream(menuState: MenuState): Flow<PagingData<Photo>> = flow {
        if (menuState.selectedItem == null) return@flow
        emitAll(
            Pager(
                config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PAGE_SIZE / 2, enablePlaceholders = false),
                pagingSourceFactory = {
                    Napier.d("instantiating photos paging source")
                    photosPagingSourceFactory(menuState)
                }
            )
                .flow
                .cachedIn(viewModelScope)
        )
    }

    fun onPhotoSelected(id: Int) {
        selectedId = id
    }
}
