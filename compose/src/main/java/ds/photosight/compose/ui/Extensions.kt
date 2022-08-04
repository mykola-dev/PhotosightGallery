package ds.photosight.compose.ui

import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import com.nesyou.staggeredgrid.StaggeredGridScope


fun <T : Any> StaggeredGridScope.pagedItems(
    items: LazyPagingItems<T>,
    itemContent: @Composable StaggeredGridScope.(value: T) -> Unit
) {
    items(
        count = items.itemCount,
    ) { index ->
        itemContent(items[index]!!)
    }
}