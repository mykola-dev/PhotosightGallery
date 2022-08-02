package ds.photosight.compose.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ds.photosight.compose.ui.model.CategoryMenuItemState
import ds.photosight.compose.ui.model.MenuState
import ds.photosight.compose.ui.model.RatingMenuItemState
import ds.photosight.parser.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

const val PAGE_SIZE = 24

class PhotosPagingSource(
    private val menuState: MenuState
) : PagingSource<Int, PhotoInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoInfo> = try {
        val key = params.key ?: 1

        val request = buildRequest(key)
        val page = withContext(Dispatchers.Default) {
            request()
        }

        Timber.d("loaded page $key, ${page.size} items")

        val prevKey = if (request is Multipage && key > 1) key - 1
        else null

        val nextKey = if (
            (key == 1 || page.size == PAGE_SIZE || request is DailyPhotosRequest)
            && request is Multipage
        ) key + 1
        else null

        Timber.d("prevKey=$prevKey nextKey=$nextKey")

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


