package ds.photosight.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.lifecycle.liveData
import ds.photosight.core.Prefs
import ds.photosight.repo.PhotosightRepo
import ds.photosight.model.toMenuItemState
import ds.photosight.repo.ResourcesRepo
import ds.photosight.parser.*
import kotlinx.coroutines.launch
import timber.log.Timber

class GalleryViewModel @ViewModelInject constructor(
    override val prefs: Prefs,
    override val log: Timber.Tree,
    private val photosightRepo: PhotosightRepo,
    private val resourcesRepo: ResourcesRepo
) : BaseViewModel() {

    private val categoriesLiveData: LiveData<List<PhotoCategory>> = liveData {
        emit(photosightRepo.getCategories())
    }

    val menuStateLiveData: LiveData<MenuState> = MutableLiveData<MenuState>(MenuState(emptyList(), emptyList()))
        .switchMap { categoriesLiveData }
        .map { categories ->
            MenuState(
                categories.map { it.toMenuItemState() }.also { it.first().isSelected = true },
                photosightRepo.getRatingsList()
            )
        }


    private val _photosStateLiveData = MediatorLiveData<PhotosState>().apply { value = PhotosState() }
    val photosStateLiveData: LiveData<PhotosState> = _photosStateLiveData

    init {
        _photosStateLiveData.addSource(menuStateLiveData) {
            _photosStateLiveData.value = _photosStateLiveData.value!!.copy(isLoading = true, photoPages = emptyList())
            loadPhotos(buildRequest())
        }
    }

    fun onMenuSelected(item: MenuItemState) {
        log.v("on selected: $item")
        (menuStateLiveData as MutableLiveData<MenuState>).value = menuStateLiveData.value!!.edit(item)

    }

    fun onFilterChanged(filter: PhotosFilter) {
        TODO()
    }

    private fun buildRequest(): PhotosRequest {
        val menuState = menuStateLiveData.value!!
        val photosState = _photosStateLiveData.value!!
        val page = photosState.currentPage
        val category = menuState.categories.find { it.isSelected }

        return when {
            category != null -> {
                CategoriesPhotosRequest(
                    category.id,
                    page,
                    photosState.categoriesFilter.sortDumpCategory,
                    photosState.categoriesFilter.sortTypeCategory
                )
            }
            else -> error("not implemented")
        }
    }

    private fun loadPhotos(request: PhotosRequest) = viewModelScope.launch {
        val photoMap = photosightRepo.apiRequest(request).associateBy { it.id }
        val photoPages = if (request is Multipage) {
            _photosStateLiveData.value!!.photoPages.toMutableList().apply { add(request.page - 1, photoMap) }
        } else {
            listOf(photoMap)
        }
        _photosStateLiveData.value = _photosStateLiveData.value!!.copy(isLoading = false, photoPages = photoPages)
    }

}

