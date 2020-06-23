package ds.photosight.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.lifecycle.liveData
import ds.photosight.core.Prefs
import ds.photosight.model.MenuItemState
import ds.photosight.repo.PhotosightRepo
import ds.photosight.model.toMenuItemState
import ds.photosight.parser.PhotoCategory
import ds.photosight.repo.ResourcesRepo
import ds.photosight.model.MenuState
import timber.log.Timber

class MenuViewModel @ViewModelInject constructor(
    override val prefs: Prefs,
    override val log: Timber.Tree,
    private val photosightRepo: PhotosightRepo,
    private val resourcesRepo: ResourcesRepo
) : BaseViewModel() {

    fun onSelected(item: MenuItemState) {
        log.v("on selected: $item")
        (menuStateLiveData as MutableLiveData<MenuState>).value = menuStateLiveData.value!!.let { state ->
            state.copy(
                categories = state
                    .categories
                    .onEach { it.isSelected = item.menu == MenuState.MENU_CATEGORIES && item.id == it.id },
                ratings = state
                    .ratings
                    .onEach { it.isSelected = item.menu == MenuState.MENU_RATINGS && item.id == it.id }

            )
        }
    }

    val menuStateLiveData: LiveData<MenuState> = MutableLiveData<MenuState>(MenuState(emptyList(), emptyList()))
        .switchMap { categoriesLiveData }
        .map { categories ->
            MenuState(
                categories.map { it.toMenuItemState() }.also { it.first().isSelected = true },
                photosightRepo.getRatingsList()
            )
        }

    private val categoriesLiveData: LiveData<List<PhotoCategory>> = liveData {
        emit(photosightRepo.getCategories())
    }

}