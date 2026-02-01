package ds.photosight.shared.repo

import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import ds.photosight.parser.*
import ds.photosight.shared.data.asUiModel
import ds.photosight.shared.ui.model.Photo
import ds.photosight.shared.ui.screen.gallery.CategoryMenuItemState
import ds.photosight.shared.ui.screen.gallery.MenuState
import ds.photosight.shared.ui.screen.gallery.RatingMenuItemState
import kotlinx.coroutines.delay

const val PAGE_SIZE = 24

interface PhotosPagingSourceFactory {
    operator fun invoke(menuState: MenuState): PhotosPagingSource
}

class PhotosPagingSource(
    private val menuState: MenuState,
    private val photosightRepo: PhotosightRepo,
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): PagingSourceLoadResult<Int, Photo> {
        return try {
            val key = params.key ?: 1

            if (key > 1) delay(1500) // 1.5s polite delay

            val request = buildRequest(key)
            val result: PhotosPage = photosightRepo.apiRequest(request)

            if (result.photos.isEmpty() && request is Multipage) {
                if (result.hasNext) {
                    throw Exception("Empty page received from server (likely throttling)")
                } else {
                    PagingSourceLoadResultPage(
                        data = emptyList(),
                        prevKey = if (key > 1) key - 1 else null,
                        nextKey = null
                    )
                }
            } else {
                val prevKey = if (request is Multipage && key > 1) key - 1 else null

                val nextKey =
                    if ((result.hasNext || request is DailyPhotosRequest) && request is Multipage)
                        key + 1
                    else null

                val paginationKey = key.toString().takeIf { request is Multipage }
                val data: List<Photo> = result.photos.map { item -> item.asUiModel(paginationKey) }
                PagingSourceLoadResultPage(
                    data = data,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            PagingSourceLoadResultError(e)
        }
    }

    private fun buildRequest(page: Int): PhotosRequest {
        return when (val selected = menuState.selectedItem ?: error("no menu item selected")) {
            is CategoryMenuItemState -> {
                val filter = menuState.categoriesFilter ?: error("filter is null")
                CategoriesPhotosRequest(
                    category = selected.category.id,
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
