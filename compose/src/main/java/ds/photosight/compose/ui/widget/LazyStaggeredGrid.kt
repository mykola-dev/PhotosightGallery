package ds.photosight.compose.ui.widget


import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.nesyou.staggeredgrid.StaggeredCells
import com.nesyou.staggeredgrid.StaggeredGridScope
import com.nesyou.staggeredgrid.StaggeredGridScopeImpl
import ds.photosight.compose.util.logCompositions
import kotlinx.coroutines.launch

class StaggeredGridState {
    var states: List<LazyListState> = emptyList()

    inline val firstVisibleItemIndex: Int get() = states.map { it.firstVisibleItemIndex }.fold(0) { acc, i -> acc + i }
    private inline val firstVisibleItemIndexFast: Int get() = states.getOrNull(0)?.firstVisibleItemIndex ?: 0
    private inline val firstVisibleItemScrollOffset get() = states.getOrNull(0)?.firstVisibleItemScrollOffset ?: 0

    suspend fun scrollToItem(index: Int) {
        if (states.isEmpty()) return
        states.forEach {
            it.scrollToItem(index / states.size)
        }
    }

    @Composable
    fun isScrollingUp(): State<Boolean> {
        logCompositions("is scrolling")
        if (states.isEmpty()) return remember { mutableStateOf(false) }

        var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndexFast) }
        var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
        return remember(this) {
            derivedStateOf {
                //log.v("derive scrolling")
                val index = firstVisibleItemIndexFast
                val offset = firstVisibleItemScrollOffset
                if (previousIndex != index) {
                    previousIndex > index
                } else {
                    previousScrollOffset >= offset
                }.also {
                    previousIndex = index
                    previousScrollOffset = offset
                }
            }
        }
    }
}

@Composable
fun TheGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    cells: StaggeredCells,
    gridState: StaggeredGridState = rememberGridState(),
    content: StaggeredGridScope.() -> Unit,
) {
    val scope = StaggeredGridScopeImpl()
    scope.apply(content)
    BoxWithConstraints(
        modifier = modifier
    ) {
        StaggeredGrid(
            scope = scope,
            padding = contentPadding,
            columnsNumber = if (cells is StaggeredCells.Fixed) cells.count else maxOf(
                (maxWidth / (cells as StaggeredCells.Adaptive).minSize).toInt(),
                1
            ),
            gridState = gridState,
        )
    }
}

@Composable
fun rememberGridState(): StaggeredGridState = remember { StaggeredGridState() }


@Composable
internal fun StaggeredGrid(
    scope: StaggeredGridScopeImpl,
    padding: PaddingValues,
    columnsNumber: Int,
    gridState: StaggeredGridState,
) {
    val states = List(columnsNumber) { rememberLazyListState() }
    gridState.states = states
    val layoutDirection = LocalLayoutDirection.current
    val coroutineScope = rememberCoroutineScope()

    val scroll = rememberScrollableState { delta ->
        coroutineScope.launch { states.forEach { it.scrollBy(-delta) } }
        delta
    }

    Row(
        modifier = Modifier
            .scrollable(
                scroll,
                Orientation.Vertical,
                flingBehavior = ScrollableDefaults.flingBehavior()
            )
    ) {
        repeat(columnsNumber) {
            LazyColumn(
                modifier = Modifier
                    .weight(1F),
                state = states[it],
                contentPadding = PaddingValues(
                    start = if (it == 0) padding.calculateLeftPadding(layoutDirection) else 0.dp,
                    end = if (it == columnsNumber - 1) padding.calculateRightPadding(
                        layoutDirection
                    ) else 0.dp,
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                ),
                userScrollEnabled = false
            ) {
                for (i in scope.content.indices step columnsNumber) {
                    if (scope.content.size > i + it) {
                        item {
                            scope.content[i + it]()
                        }
                    }
                }
            }
        }
    }

}
