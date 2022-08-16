package ds.photosight.compose.ui.screen.gallery

import androidx.compose.material.BottomSheetValue
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import ds.photosight.compose.data.asUiModel
import ds.photosight.compose.repo.PhotosightRepo
import ds.photosight.compose.ui.BaseViewModel
import ds.photosight.compose.ui.events.UiEvent
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.model.PhotosFilter
import ds.photosight.compose.usecase.CheckVersionUseCase
import ds.photosight.compose.usecase.ToolbarDataUseCase
import ds.photosight.parser.PhotoCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val photosightRepo: PhotosightRepo,
    private val toolbarDataUseCase: ToolbarDataUseCase,
    checkVersionUseCase: CheckVersionUseCase,
    log: Timber.Tree
) : BaseViewModel(log) {

    private val _galleryState = MutableStateFlow(
        GalleryState(
            title = toolbarDataUseCase.getTitle(),
            subtitle = toolbarDataUseCase.getSubtitle(),
            showAboutDialog = checkVersionUseCase.shouldShowAboutDialog()
        )
    )
    val galleryState: StateFlow<GalleryState> = _galleryState.asStateFlow()

    private val categoriesFlow: Flow<List<PhotoCategory>> = flow {
        emit(photosightRepo.getCategories())
    }.retry {
        it.printStackTrace()
        log.e("retry!")
        delay(2000)
        true
    }

    private val _menuStateFlow: MutableStateFlow<MenuState> = MutableStateFlow(MenuState())
    val menuStateFlow: StateFlow<MenuState> = _menuStateFlow.asStateFlow()

    init {
        launch {
            categoriesFlow.collect { categories ->
                val menuState = MenuState(
                    categories = categories.map { it.asUiModel() },
                    ratings = photosightRepo.getRatingsList(),
                )
                _menuStateFlow.value = menuState
            }
        }
        launch {
            menuStateFlow.collect { menu ->
                _galleryState.update {
                    it.copy(title = toolbarDataUseCase.getTitle(menu))
                }
            }
        }

    }

    fun onMenuSelected(item: MenuItemState) {
        _menuStateFlow.update { state ->
            state.copy(
                selectedItem = item,
                bottomSheetState = BottomSheetValue.Collapsed,
            )
        }
    }

    fun onFilterChanged(filter: PhotosFilter) {
        _menuStateFlow.update { state ->
            when (filter) {
                is PhotosFilter.Categories -> state.copy(categoriesFilter = filter)
                else -> error("not supported")
            }
        }
    }

    fun updateErrorState(state: CombinedLoadStates) {
        val hasError = state.refresh is LoadState.Error || state.append is LoadState.Error || state.prepend is LoadState.Error
        if (hasError) event(UiEvent.Retry())
    }

    fun updateLoadingState(state: CombinedLoadStates) {
        val loading = state.refresh is LoadState.Loading
            || state.append is LoadState.Loading
            || state.prepend is LoadState.Loading
            || menuStateFlow.value.selectedItem == null

        _galleryState.update { it.copy(isLoading = loading) }
    }

    fun setFirstVisibleItem(photoInfo: Photo) {
        _galleryState.update { it.copy(subtitle = toolbarDataUseCase.getSubtitle(photoInfo)) }
    }

    fun onShowAboutDialog() {
        _galleryState.update { it.copy(showAboutDialog = true) }
    }

    fun onDismissAboutDialog() {
        _galleryState.update { it.copy(showAboutDialog = false) }
    }

}

