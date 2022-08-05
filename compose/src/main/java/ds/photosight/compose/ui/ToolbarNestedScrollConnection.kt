package ds.photosight.compose.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.AppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import timber.log.Timber

@Composable
fun rememberToolbarNestedScrollConnection(): ToolbarNestedScrollConnection {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val toolbarHeight = 56.dp + statusBarHeight
    val density = LocalDensity.current

    return remember(toolbarHeight) {
        Timber.v("connection created")
        ToolbarNestedScrollConnection(toolbarHeight, density)
    }
}

class ToolbarNestedScrollConnection(val toolbarHeight: Dp, density: Density) : NestedScrollConnection {
    private val toolbarHeightPx = with(density) { toolbarHeight.toPx() }
    val toolbarOffsetHeightPx = mutableStateOf(0f)

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y
        val newOffset = toolbarOffsetHeightPx.value + delta
        toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, 0f)
        return Offset.Zero
    }
}