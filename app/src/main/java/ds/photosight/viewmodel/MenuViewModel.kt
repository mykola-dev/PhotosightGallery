package ds.photosight.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.lifecycle.liveData
import ds.photosight.utils.liveData
import ds.photosight.core.Prefs
import ds.photosight.repo.PhotosightRepo
import ds.photosight.model.toMenuItemState
import ds.photosight.parser.PhotoCategory
import ds.photosight.repo.ResourcesRepo
import ds.photosight.view.MenuItemState
import ds.photosight.view.MenuState

class MenuViewModel @ViewModelInject constructor(
    override val prefs: Prefs,
    private val photosightRepo: PhotosightRepo,
    private val resourcesRepo: ResourcesRepo
) : BaseViewModel() {

    val menuState: LiveData<MenuState> = MutableLiveData<MenuState>(MenuState(emptyList(), emptyList()))
        .switchMap { categories }
        .map { MenuState(it.mapIndexed { idx, value -> value.toMenuItemState(idx) }, photosightRepo.getRatingsList()) }

    val categories: LiveData<List<PhotoCategory>> = liveData {
        emit(photosightRepo.getCategories())
    }

}