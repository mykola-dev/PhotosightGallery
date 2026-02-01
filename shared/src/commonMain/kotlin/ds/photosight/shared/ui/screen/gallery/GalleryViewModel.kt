@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package ds.photosight.shared.ui.screen.gallery

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import ds.photosight.shared.data.asUiModel
import ds.photosight.shared.repo.PhotosightRepo
import ds.photosight.shared.ui.BaseViewModel
import ds.photosight.shared.ui.events.UiEvent
import ds.photosight.shared.usecase.CheckVersionUseCase
import ds.photosight.shared.usecase.ToolbarDataUseCase
import ds.photosight.parser.CategoriesPhotosRequest
import ds.photosight.parser.PhotoCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import io.github.aakira.napier.Napier

class GalleryViewModel(
        private val photosightRepo: PhotosightRepo,
        private val toolbarDataUseCase: ToolbarDataUseCase,
        checkVersionUseCase: CheckVersionUseCase,
) : BaseViewModel() {

    private val _galleryState =
            MutableStateFlow(
                    GalleryState(
                            title = "",
                            subtitle = toolbarDataUseCase.getSubtitle(),
                            showAboutDialog = checkVersionUseCase.shouldShowAboutDialog()
                    )
            )
    val galleryState: StateFlow<GalleryState> = _galleryState.asStateFlow()

    private val categoriesFlow: Flow<List<PhotoCategory>> =
            flow { emit(photosightRepo.getCategories()) }.retry {
                it.printStackTrace()
                Napier.e("retry!")
                delay(2000)
                true
            }

    private val _menuStateFlow: MutableStateFlow<MenuState> = MutableStateFlow(MenuState())
    val menuStateFlow: StateFlow<MenuState> = _menuStateFlow.asStateFlow()

    init {
        launch {
            categoriesFlow.collect { categories ->
                val menuState =
                        MenuState(
                                categories = categories.mapNotNull { it.asUiModel() },
                                ratings = photosightRepo.getRatingsList(),
                        )
                _menuStateFlow.value = menuState
            }
        }
        launch {
            menuStateFlow.collect { menu ->
                _galleryState.update { it.copy(title = toolbarDataUseCase.getTitle(menu)) }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun onMenuSelected(item: MenuItemState) {
        _menuStateFlow.update { state ->
            state.copy(
                    selectedItem = item,
                    bottomSheetState = SheetValue.PartiallyExpanded,
                    categoriesFilter = if (item is CategoryMenuItemState) PhotosFilter() else null
            )
        }
    }

    fun onFilterSelected(filter: CategoriesPhotosRequest.FilterDumpCategory) {
        _menuStateFlow.update { state ->
            state.copy(categoriesFilter = state.categoriesFilter?.copy(filterDumpCategory = filter))
        }
    }

    fun onSorterSelected(sorter: CategoriesPhotosRequest.SortTypeCategory) {
        _menuStateFlow.update { state ->
            state.copy(categoriesFilter = state.categoriesFilter?.copy(sortTypeCategory = sorter))
        }
    }

    fun updateErrorState(state: CombinedLoadStates) {
        val hasError =
                state.refresh is LoadState.Error ||
                        state.append is LoadState.Error ||
                        state.prepend is LoadState.Error
        if (hasError) event(UiEvent.Retry())
    }

    fun updateLoadingState(state: CombinedLoadStates) {
        val loading =
                state.refresh is LoadState.Loading ||
                        state.append is LoadState.Loading ||
                        state.prepend is LoadState.Loading ||
                        menuStateFlow.value.selectedItem == null

        _galleryState.update { it.copy(isLoading = loading) }
    }

    fun setFirstVisibleItem(photoInfo: ds.photosight.shared.ui.model.Photo) {
        _galleryState.update { it.copy(subtitle = toolbarDataUseCase.getSubtitle(photoInfo)) }
    }

    fun onShowAboutDialog() {
        _galleryState.update { it.copy(showAboutDialog = true) }
    }

    fun onDismissAboutDialog() {
        _galleryState.update { it.copy(showAboutDialog = false) }
    }
}
