package ds.photosight.compose.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import ds.photosight.compose.data.asUiModel
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.screen.gallery.CategoryMenuItemState
import ds.photosight.compose.ui.screen.gallery.MenuState
import ds.photosight.compose.ui.screen.gallery.RatingMenuItemState
import ds.photosight.parser.CategoriesPhotosRequest
import ds.photosight.parser.DailyPhotosRequest
import ds.photosight.parser.DatePage
import ds.photosight.parser.Multipage
import ds.photosight.parser.NewPhotosRequest
import ds.photosight.parser.PhotosPage
import ds.photosight.parser.PhotosRequest
import ds.photosight.parser.SimplePage
import ds.photosight.parser.Top200PhotosRequest
import ds.photosight.parser.Top50PhotosRequest
import ds.photosight.parser.TopApplicantsPhotosRequest
import ds.photosight.parser.TopFavoritesPhotosRequest
import java.io.IOException

const val PAGE_SIZE = 24

interface PhotosPagingSourceFactory {
    operator fun invoke(menuState: MenuState): PhotosPagingSource
}

class PhotosPagingSource(
        private val menuState: MenuState,
        private val photosightRepo: PhotosightRepo,
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            val key = params.key ?: 1

            if (key > 1) kotlinx.coroutines.delay(1500) // 1.5s polite delay

            val request = buildRequest(key)
            val result: PhotosPage = photosightRepo.apiRequest(request)

            if (result.photos.isEmpty() && request is Multipage) {
                if (result.hasNext) {
                    throw IOException("Empty page received from server (likely throttling)")
                } else {
                    LoadResult.Page(emptyList(), if (key > 1) key - 1 else null, null)
                }
            } else {
                val prevKey = if (request is Multipage && key > 1) key - 1 else null

                val nextKey =
                        if ((result.hasNext || request is DailyPhotosRequest) &&
                                        request is Multipage
                        )
                                key + 1
                        else null

                val paginationKey = key.toString().takeIf { request is Multipage }
                val data: List<Photo> = result.photos.map { item -> item.asUiModel(paginationKey) }
                LoadResult.Page(data, prevKey, nextKey)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    private fun buildRequest(page: Int): PhotosRequest {
        return when (val selected = menuState.selectedItem ?: error("no menu item selected")) {
            is CategoryMenuItemState -> {
                val filter = menuState.categoriesFilter ?: error("filter is null")
                CategoriesPhotosRequest(
                        category = selected.category,
                        page = SimplePage(page),
                        filterDumpCategory = filter.filterDumpCategory,
                        sortTypeCategory = filter.sortTypeCategory
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

fun LazyPagingItems<Photo>.getIndexById(selectedId: Int): Int? =
        itemSnapshotList.indexOfFirst { it?.id == selectedId }.takeIf { it >= 0 }
