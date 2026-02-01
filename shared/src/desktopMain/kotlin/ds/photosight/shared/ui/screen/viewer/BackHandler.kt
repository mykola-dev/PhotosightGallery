package ds.photosight.shared.ui.screen.viewer

import androidx.compose.runtime.Composable

/**
 * Desktop implementation of BackHandler - no-op since desktop doesn't have a hardware back button.
 */
@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-op on desktop - use window close button or Esc key handling instead
}
