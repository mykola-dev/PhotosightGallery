package ds.photosight.compose.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

@Composable
fun PhotosightTheme(
    content: @Composable () -> Unit
) {
    val colors = darkColors(
        primary = Palette.primary,
        secondary = Palette.accent,
        background = Palette.background,
        surface = Palette.primary,
    )

    MaterialTheme(
        //typography = Typography,
        content = content,
        colors = colors
    )
}