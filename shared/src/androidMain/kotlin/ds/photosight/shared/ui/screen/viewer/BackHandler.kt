package ds.photosight.shared.ui.screen.viewer

import androidx.compose.runtime.Composable

/**
 * Android implementation of BackHandler using androidx.activity.compose.BackHandler.
 */
@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(enabled = enabled, onBack = onBack)
}
