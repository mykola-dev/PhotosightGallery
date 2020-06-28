package ds.photosight.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import ds.photosight.core.Prefs
import ds.photosight.repo.PhotosightRepo
import ds.photosight.model.toMenuItemState
import ds.photosight.repo.ResourcesRepo
import ds.photosight.parser.*
import ds.photosight.repo.PhotosPagingSource
import kotlinx.coroutines.delay
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


    private val _photosStateLiveData = MediatorLiveData<PagingData<PhotoInfo>>().apply { value = PagingData.empty() }
    val photosStateLiveData: LiveData<PagingData<PhotoInfo>> = _photosStateLiveData

    private val photosPagedLiveData: LiveData<PagingData<PhotoInfo>> = menuStateLiveData.switchMap {
        Pager(
            config = PagingConfig(pageSize = 24, prefetchDistance = 12),
            pagingSourceFactory = {
                Timber.d("instantiating photos paging source")
                PhotosPagingSource(menuStateLiveData.value!!)
            }
        ).liveData
    }

    val loadingState = MutableLiveData<Boolean>()

    init {
        _photosStateLiveData.addSource(menuStateLiveData) {
            _photosStateLiveData.value = PagingData.empty()
            loadingState.value = true
        }
        _photosStateLiveData.addSource(photosPagedLiveData) { pagingData ->
            _photosStateLiveData.value = pagingData
            loadingState.value = false
        }
    }


    fun onMenuSelected(item: MenuItemState) {
        log.v("on selected: $item")
        (menuStateLiveData as MutableLiveData<MenuState>).value = menuStateLiveData.value!!.edit(item)

    }

    fun onFilterChanged(filter: PhotosFilter) {
        TODO()
    }


}

