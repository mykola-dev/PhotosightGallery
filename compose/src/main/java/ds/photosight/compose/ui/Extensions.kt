package ds.photosight.compose.ui

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
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

fun <T : Any> LazyGridScope.pagedItems(
    items: LazyPagingItems<T>,
    key: ((index: Int) -> Any)? = null,
    itemContent: @Composable LazyGridItemScope.(value: T) -> Unit
) {
    items(
        count = items.itemCount,
        key = key,
    ) { index ->
        itemContent(items[index]!!)
    }
}

/**
 * Used to isolate re-compositions inside one big composable
 */
@Composable
fun <T> isolate(
    executable: (@Composable () -> T),
    content: @Composable (T) -> Unit
) {
    content(executable())
}

@Composable
fun isolate(
    content: @Composable () -> Unit
) {
    content()
}

