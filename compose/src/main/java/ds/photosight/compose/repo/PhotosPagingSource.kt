package ds.photosight.compose.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ds.photosight.compose.data.asUiModel
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.screen.gallery.CategoryMenuItemState
import ds.photosight.compose.ui.screen.gallery.MenuState
import ds.photosight.compose.ui.screen.gallery.RatingMenuItemState
import ds.photosight.parser.*

const val PAGE_SIZE = 24

@AssistedFactory
interface PhotosPagingSourceFactory {
    operator fun invoke(menuState: MenuState): PhotosPagingSource
}

class PhotosPagingSource @AssistedInject constructor(
    @Assisted private val menuState: MenuState,
    private val photosightRepo: PhotosightRepo,
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> = try {
        val key = params.key ?: 1

        val request = buildRequest(key)
        val page: List<PhotoInfo> = photosightRepo.apiRequest(request)

        val prevKey = if (request is Multipage && key > 1) key - 1
        else null

        val nextKey = if (
            (key == 1 || page.size == PAGE_SIZE || request is DailyPhotosRequest)
            && request is Multipage
        ) key + 1
        else null

        val data: List<Photo> = page.map { item -> item.asUiModel() }
        LoadResult.Page(data, prevKey, nextKey)
    } catch (e: Exception) {
        e.printStackTrace()
        LoadResult.Error(e)
    }

    private fun buildRequest(page: Int): PhotosRequest {
        return when (val selected = menuState.selectedItem ?: error("no menu item selected")) {
            is CategoryMenuItemState -> {
                val filter = menuState.categoriesFilter ?: error("filter is null")
                CategoriesPhotosRequest(
                    selected.category,
                    SimplePage(page),
                    filter.filterDumpCategory,
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

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        throw UnsupportedOperationException("not implemented")
    }
}

fun LazyPagingItems<Photo>.getIndexById(selectedId: Int): Int? = itemSnapshotList.indexOfFirst { it?.id == selectedId }.takeIf { it >= 0 }
