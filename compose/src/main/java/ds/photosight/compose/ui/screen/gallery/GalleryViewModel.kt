package ds.photosight.compose.ui.screen.gallery

import androidx.compose.material.BottomSheetValue
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import ds.photosight.compose.data.toMenuItemState
import ds.photosight.compose.repo.PhotosightRepo
import ds.photosight.compose.ui.BaseViewModel
import ds.photosight.compose.ui.model.MenuItemState
import ds.photosight.compose.ui.model.MenuState
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.model.PhotosFilter
import ds.photosight.compose.usecase.CheckVersionUseCase
import ds.photosight.compose.util.AppNameProvider
import ds.photosight.compose.util.SingleEvent
import ds.photosight.parser.PhotoCategory
import ds.photosight.parser.PhotoInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class RetryEvent()

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val appNameProvider: AppNameProvider,
    private val photosightRepo: PhotosightRepo,
    checkVersionUseCase: CheckVersionUseCase,
    log: Timber.Tree
) : BaseViewModel(log) {

    val showAboutDialog = mutableStateOf(checkVersionUseCase.shouldShowAboutDialog())

    val isLoading = mutableStateOf(true)
    val firstVisibleItem: MutableState<Photo?> = mutableStateOf(null)

    val retryEvent = SingleEvent<RetryEvent>()

    private val categoriesFlow: Flow<List<PhotoCategory>> = flow {
        emit(photosightRepo.getCategories())
    }.retry {
        it.printStackTrace()
        log.e("retry!")
        delay(2000)
        true
    }

    private val mutableMenuStateFlow: MutableStateFlow<MenuState> = MutableStateFlow(MenuState())

    val menuStateFlow: StateFlow<MenuState> = mutableMenuStateFlow.asStateFlow()

    val title: Flow<String> = menuStateFlow.map {
        it.selectedItem?.title ?: appNameProvider()
    }

    init {
        launch {
            categoriesFlow.collect { categories ->
                val menuState = MenuState(
                    categories = categories.map { it.toMenuItemState() },
                    ratings = photosightRepo.getRatingsList(),
                )
                mutableMenuStateFlow.value = menuState
            }
        }
    }


    fun onMenuSelected(item: MenuItemState) {
        mutableMenuStateFlow.update { state ->
            state.copy(
                selectedItem = item,
                bottomSheetState = BottomSheetValue.Collapsed,
            )
        }
    }

    fun onFilterChanged(filter: PhotosFilter) {
        mutableMenuStateFlow.update { state ->
            when (filter) {
                is PhotosFilter.Categories -> state.copy(categoriesFilter = filter)
                else -> error("not supported")
            }
        }
    }

    fun onPhotoClicked(photo: Photo) {
        log.v("todo")
    }

    fun updateErrorState(state: CombinedLoadStates) {
        val hasError = state.refresh is LoadState.Error || state.append is LoadState.Error || state.prepend is LoadState.Error
        if (hasError) retryEvent(RetryEvent())
    }

    fun updateLoadingState(state: CombinedLoadStates) {
        val loading = state.refresh is LoadState.Loading
            || state.append is LoadState.Loading
            || state.prepend is LoadState.Loading
            || menuStateFlow.value.selectedItem == null

        isLoading.value = loading
    }

    fun setFirstVisibleItem(photoInfo: Photo) {
        firstVisibleItem.value = photoInfo
    }

}

