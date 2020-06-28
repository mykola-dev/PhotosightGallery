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

    val photosPagedLiveData: LiveData<PagingData<PhotoInfo>> = menuStateLiveData
        .switchMap {
            liveData {
                emit(PagingData.empty())
                emitSource(
                    Pager(
                        config = PagingConfig(pageSize = 24, prefetchDistance = 12, enablePlaceholders = false),
                        pagingSourceFactory = {
                            Timber.d("instantiating photos paging source")
                            PhotosPagingSource(menuStateLiveData.value!!)
                        }
                    ).liveData
                )
            }

        }

    val loadingState = MutableLiveData<Boolean>(true)


    fun onMenuSelected(item: MenuItemState) {
        (menuStateLiveData as MutableLiveData<MenuState>).reduce(item)
    }

    fun onFilterChanged(filter: PhotosFilter) {
        TODO()
    }


}

fun MutableLiveData<MenuState>.reduce(item: MenuItemState) = with(value!!) {
    value = copy(
        categories = categories.onEach { it.isSelected = item.menu == MenuState.MENU_CATEGORIES && item.id == it.id },
        ratings = ratings.onEach { it.isSelected = item.menu == MenuState.MENU_RATINGS && item.id == it.id }
    )
}