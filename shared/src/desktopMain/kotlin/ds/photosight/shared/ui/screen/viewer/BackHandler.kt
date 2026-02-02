package ds.photosight.shared.ui.screen.viewer

import androidx.compose.runtime.Composable

/**
 * Desktop/non-Android implementation of BackHandler - no-op.
 * Desktop apps handle navigation via window controls or software buttons.
 */
@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-op: No hardware back button on desktop
}
