package ds.photosight.shared.ui

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
@Suppress("ComposableNaming")
@Composable
fun <T> isolate(
    executable: (@Composable () -> T),
    content: @Composable (T) -> Unit
) {
    content(executable())
}

fun <T : Any> LazyPagingItems<T>.getOrNull(index: Int): T? = if (index < itemCount) get(index) else null

fun <T : Any> LazyPagingItems<T>.getIndexById(id: Int?): Int? {
    if (id == null) return null
    for (i in 0 until itemCount) {
        val item = get(i)
        // This is a bit tricky with PagingData as it might not have all items loaded.
        // But for our needs it should work if the item is in the current page.
        // Assuming T has an 'id' property or we need a more generic way.
        // For now, let's cast to Photo if possible.
        if (item is ds.photosight.shared.ui.model.Photo && item.id == id) {
            return i
        }
    }
    return null
}
