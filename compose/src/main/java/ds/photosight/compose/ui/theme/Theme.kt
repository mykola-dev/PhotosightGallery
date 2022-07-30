package ds.photosight.compose.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

private val colors = darkColors(
    primary = Palette.primary,
    secondary = Palette.secondary,
    background = Palette.background,
    surface = Palette.surface,
)

private val shapes = Shapes(
    large = RoundedCornerShape(8.dp)
)

@Composable
fun PhotosightTheme(
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(LocalElevationOverlay provides null) {
        MaterialTheme(
            content = content,
            colors = colors,
            shapes = shapes
        )
    }

}