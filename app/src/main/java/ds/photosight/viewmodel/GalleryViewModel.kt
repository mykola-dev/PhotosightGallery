package ds.photosight.viewmodel

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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import timber.log.Timber

class GalleryViewModel @ViewModelInject constructor(
    override val prefs: Prefs,
    override val log: Timber.Tree,
    private val photosightRepo: PhotosightRepo,
    private val resourcesRepo: ResourcesRepo
) : BaseViewModel() {

    val retrySnackbarCommand = LiveEvent<String>()

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
                categories.map { it.toMenuItemState() }.also { it.first().isSelected = true },
                photosightRepo.getRatingsList()
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
        retrySnackbarCommand("Loading Failed")
    }


}

fun MutableLiveData<MenuState>.reduce(item: MenuItemState) = with(value!!) {
    value = copy(
        categories = categories.onEach { it.isSelected = item.menu == MenuState.MENU_CATEGORIES && item.id == it.id },
        ratings = ratings.onEach { it.isSelected = item.menu == MenuState.MENU_RATINGS && item.id == it.id }
    )
}