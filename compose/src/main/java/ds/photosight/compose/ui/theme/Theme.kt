package ds.photosight.compose.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

private val colors = darkColors(
    primary = Palette.primary,
    secondary = Palette.secondary,
    background = Palette.background,
    surface = Palette.surface,
)
val viewerColors = colors.copy(surface = Palette.translucent)

private val shapes = Shapes(
    large = RoundedCornerShape(8.dp)
)

@Composable
fun PhotosightTheme(
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(
        LocalElevationOverlay provides null,
    ) {
        MaterialTheme(
            content = content,
            colors = colors,
            shapes = shapes
        )
    }
}

@Composable
fun TranslucentTheme(
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(
        LocalElevationOverlay provides null,
    ) {
        MaterialTheme(
            content = content,
            colors = viewerColors,
            shapes = shapes
        )
    }
}