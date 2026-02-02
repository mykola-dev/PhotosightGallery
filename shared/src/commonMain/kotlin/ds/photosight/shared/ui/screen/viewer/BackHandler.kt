package ds.photosight.shared.ui.screen.viewer

import androidx.compose.runtime.Composable

/**
 * Multiplatform BackHandler using expect/actual pattern.
 * Android: Handles hardware back button via androidx.activity.compose.BackHandler
 * Other platforms: No-op (no hardware back button)
 */
@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)
