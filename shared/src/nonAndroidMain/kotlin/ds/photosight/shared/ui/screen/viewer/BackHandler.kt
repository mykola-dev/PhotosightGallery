package ds.photosight.shared.ui.screen.viewer

import androidx.compose.runtime.Composable

/**
 * Non-Android implementation of BackHandler - no-op since there's no hardware back button.
 */
@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-op on non-Android platforms
}
