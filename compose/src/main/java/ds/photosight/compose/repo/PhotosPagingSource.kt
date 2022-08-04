package ds.photosight.compose.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ds.photosight.compose.ui.model.CategoryMenuItemState
import ds.photosight.compose.ui.model.MenuState
import ds.photosight.compose.ui.model.RatingMenuItemState
import ds.photosight.parser.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

const val PAGE_SIZE = 24

@AssistedFactory
interface PhotosPagingSourceFactory {
    operator fun invoke(menuState: MenuState): PhotosPagingSource
}

class PhotosPagingSource @AssistedInject constructor(
    @Assisted private val menuState: MenuState,
    private val photosightRepo: PhotosightRepo,
) : PagingSource<Int, PhotoInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoInfo> = try {
        val key = params.key ?: 1

        val request = buildRequest(key)
        val page = photosightRepo.apiRequest(request)

        val prevKey = if (request is Multipage && key > 1) key - 1
        else null

        val nextKey = if (
            (key == 1 || page.size == PAGE_SIZE || request is DailyPhotosRequest)
            && request is Multipage
        ) key + 1
        else null

        LoadResult.Page(page, prevKey, nextKey)
    } catch (e: Exception) {
        e.printStackTrace()
        LoadResult.Error(e)
    }

    private fun buildRequest(page: Int): PhotosRequest {
        return when (val selected = menuState.selectedItem ?: error("no menu item selected")) {
            is CategoryMenuItemState -> {
                val filter = menuState.categoriesFilter
                CategoriesPhotosRequest(
                    selected.category,
                    SimplePage(page),
                    filter.sortDumpCategory,
                    filter.sortTypeCategory
                )
            }
            is RatingMenuItemState -> {
                when (selected.type) {
                    RatingMenuItemState.Type.ALL -> NewPhotosRequest(SimplePage(page))
                    RatingMenuItemState.Type.DAY -> DailyPhotosRequest(DatePage(page))
                    RatingMenuItemState.Type.WEEK -> Top50PhotosRequest()
                    RatingMenuItemState.Type.MONTH -> Top200PhotosRequest()
                    RatingMenuItemState.Type.FAVS -> TopFavoritesPhotosRequest()
                    RatingMenuItemState.Type.APPLICANTS -> TopApplicantsPhotosRequest()
                }
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoInfo>): Int? {
        throw UnsupportedOperationException("not implemented")
    }
}


