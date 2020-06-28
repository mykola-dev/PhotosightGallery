package ds.photosight.repo

import androidx.paging.PagingSource
import ds.photosight.parser.CategoriesPhotosRequest
import ds.photosight.parser.Multipage
import ds.photosight.parser.PhotoInfo
import ds.photosight.parser.PhotosRequest
import ds.photosight.viewmodel.MenuState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PhotosPagingSource(
    private val menuState: MenuState
) : PagingSource<Int, PhotoInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoInfo> = try {
        val key = params.key ?: 1

        val request = buildRequest(key)
        val page = withContext(Dispatchers.Default) { request() }

        Timber.d("loaded page $key, ${page.size} items")

        val prevKey = if (request is Multipage && key > 1) key - 1
        else null

        val nextKey = if (page.isNotEmpty() && request is Multipage) key + 1
        else null

        LoadResult.Page(page, prevKey, nextKey)
    } catch (e: Exception) {
        e.printStackTrace()
        LoadResult.Error(e)
    }

    private fun buildRequest(page: Int): PhotosRequest {
        val category = menuState.categories.find { it.isSelected }

        return when {
            category != null -> {
                CategoriesPhotosRequest(
                    category.id,
                    page,
                    menuState.categoriesFilter.sortDumpCategory,
                    menuState.categoriesFilter.sortTypeCategory
                )
            }
            else -> error("not implemented")
        }
    }
}


