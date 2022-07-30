package ds.photosight.compose.ui.screen.gallery

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import ds.photosight.compose.data.toMenuItemState
import ds.photosight.compose.repo.PhotosightRepo
import ds.photosight.compose.ui.BaseViewModel
import ds.photosight.compose.ui.model.MenuItemState
import ds.photosight.compose.ui.model.MenuState
import ds.photosight.compose.ui.model.PhotosFilter
import ds.photosight.compose.usecase.CheckVersionUseCase
import ds.photosight.compose.util.AppNameProvider
import ds.photosight.parser.PhotoCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val appNameProvider: AppNameProvider,
    private val photosightRepo: PhotosightRepo,
    checkVersionUseCase: CheckVersionUseCase,
    val log: Timber.Tree
) : BaseViewModel() {

    val appName: String get() = appNameProvider()

    val showAboutDialog = mutableStateOf(checkVersionUseCase.shouldShowAboutDialog())

    val isLoading = mutableStateOf(true)

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
            state.copy(selectedItem = item)
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

}

