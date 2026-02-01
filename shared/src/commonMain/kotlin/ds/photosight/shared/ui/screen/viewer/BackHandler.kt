package ds.photosight.shared.ui.screen.viewer

import androidx.compose.runtime.Composable

/**
 * Multiplatform BackHandler using expect/actual pattern.
 * Handles back button press on Android, no-op on other platforms.
 */
@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)
