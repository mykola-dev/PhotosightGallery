package ds.photosight.compose.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems

fun <T : Any> LazyStaggeredGridScope.pagedItems(
    items: LazyPagingItems<T>,
    itemContent: @Composable LazyStaggeredGridScope.(value: T) -> Unit
) {
    val gridScope = this
    items(
        count = items.itemCount,
    ) { index ->
        items[index]?.let { item ->
            // Call the extension function explicitly with the captured scope
            gridScope.itemContent(item)
        }
    }
}

/**
 * Used to isolate re-compositions inside one big composable
 */
@SuppressLint("ComposableNaming")
@Composable
fun <T> isolate(
    executable: (@Composable () -> T),
    content: @Composable (T) -> Unit
) {
    content(executable())
}

fun <T : Any> LazyPagingItems<T>.getOrNull(index: Int): T? = if (index < itemCount) get(index) else null