package ds.photosight.compose.ui.screen.viewer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.unit.dp

@Composable
fun ViewerBottomBar(
    isVisible: Boolean,
    isExpanded: State<Boolean>,
    onDrawerClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onBrowserClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        BottomAppBar(
            elevation = 0.dp,
            cutoutShape = if (!isExpanded.value) CircleShape else RoundedCornerShape(10),
            contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        ) {
            IconButton(onDrawerClick) {
                Icon(Icons.Default.Menu, null)
            }
            IconButton(onDownloadClick) {
                Icon(Icons.Default.Save, null)
            }
            IconButton(onBrowserClick) {
                Icon(Icons.Default.OpenInBrowser, null)
            }
            IconButton(onInfoClick) {
                Icon(Icons.Default.Info, null)
            }

        }
    }
}