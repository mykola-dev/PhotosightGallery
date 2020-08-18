package ds.photosight.repo

import androidx.paging.PagingSource
import ds.photosight.parser.*
import ds.photosight.ui.viewmodel.CategoryMenuItemState
import ds.photosight.ui.viewmodel.MenuState
import ds.photosight.ui.viewmodel.PhotosFilter
import ds.photosight.ui.viewmodel.RatingMenuItemState
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
            request().ifEmpty {
                // https://issuetracker.google.com/issues/161925081
                listOf(PhotoInfo(0, "", "", "", "", "", "", null))
            }
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
        return when (val selected = menuState.getSelected()) {
            is CategoryMenuItemState -> {
                val filter = menuState.categoriesFilter
                CategoriesPhotosRequest(
                    selected.category,
                    SimplePage(page),
                    filter.sortDumpCategory,
                    filter.sortTypeCategory
                )
            }
            is RatingMenuItemState.All -> NewPhotosRequest(SimplePage(page))
            is RatingMenuItemState.Day -> DailyPhotosRequest(DatePage(page))
            is RatingMenuItemState.Week -> Top50PhotosRequest()
            is RatingMenuItemState.Month -> Top200PhotosRequest()
            is RatingMenuItemState.Art -> TopArtPhotosRequest()
            is RatingMenuItemState.Orig -> TopOrigPhotosRequest()
            is RatingMenuItemState.Tech -> TopTechPhotosRequest()
            is RatingMenuItemState.Favs -> TopFavoritesPhotosRequest()
            is RatingMenuItemState.Applicants -> TopApplicantsPhotosRequest()
            is RatingMenuItemState.Outrun -> OutrunPhotosRequest()
            else -> error("illegal menu item")
        }
    }
}


