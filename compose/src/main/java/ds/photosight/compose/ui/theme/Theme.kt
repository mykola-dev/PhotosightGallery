package ds.photosight.compose.ui.theme

import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun PhotosightTheme(
    content: @Composable () -> Unit
) {
    val colors = darkColors(
        primary = Palette.primary,
        secondary = Palette.secondary,
        background = Palette.background,
        surface = Palette.surface,

    )

    CompositionLocalProvider(LocalElevationOverlay provides null) {
        MaterialTheme(
            content = content,
            colors = colors
        )
    }

}