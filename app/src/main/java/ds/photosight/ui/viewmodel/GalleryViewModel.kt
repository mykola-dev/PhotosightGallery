package ds.photosight.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.lifecycle.liveData
import com.hadilq.liveevent.LiveEvent
import ds.photosight.core.Prefs
import ds.photosight.repo.PhotosightRepo
import ds.photosight.model.toMenuItemState
import ds.photosight.repo.ResourcesRepo
import ds.photosight.parser.*
import ds.photosight.utils.invoke
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import timber.log.Timber

class GalleryViewModel @ViewModelInject constructor(
    override val prefs: Prefs,
    override val log: Timber.Tree,
    private val photosightRepo: PhotosightRepo,
    private val resourcesRepo: ResourcesRepo
) : BaseViewModel() {

    val retrySnackbarCommand = LiveEvent<Unit>()

    private val categoriesLiveData: LiveData<List<PhotoCategory>> = liveData {
        flow { emit(photosightRepo.getCategories()) }
            .retry {
                log.w("retry!")
                delay(2000)
                true
            }
            .asLiveData(coroutineContext)
            .also { emitSource(it) }
    }

    val menuStateLiveData: LiveData<MenuState> = MutableLiveData<MenuState>(MenuState(emptyList(), emptyList()))
        .switchMap { categoriesLiveData }
        .map { categories ->
            MenuState(
                categories.map { it.toMenuItemState() },
                photosightRepo.getRatingsList().also { it.first().isSelected = true }
            )
        }


    val loadingState = MutableLiveData<Boolean>(true)


    fun onMenuSelected(item: MenuItemState) {
        (menuStateLiveData as MutableLiveData<MenuState>).reduce(item)
    }

    fun onFilterChanged(filter: PhotosFilter) {
        TODO()
    }

    fun onLoadingError() {
        retrySnackbarCommand()
    }


}

fun MutableLiveData<MenuState>.reduce(item: MenuItemState) = with(value!!) {
    value = copy(
        categories = categories.onEach { it.isSelected = item is CategoryMenuItemState && item.category == it.category },
        ratings = ratings.onEach { it.isSelected = item.javaClass == it.javaClass }
    )
}