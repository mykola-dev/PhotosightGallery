package ds.photosight.shared.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

private val DarkColorScheme =
        darkColorScheme(
                primary = Palette.primary,
                secondary = Palette.secondary,
                background = Palette.background,
                surface = Palette.surface,
                onPrimary = Palette.background,
                onSecondary = Palette.background,
                onBackground = Palette.primary,
                onSurface = Palette.primary,
        )

private val TranslucentColorScheme =
        DarkColorScheme.copy(surface = Palette.translucent, onSurface = Palette.primary)

private val Shapes = Shapes(medium = RoundedCornerShape(8.dp), large = RoundedCornerShape(12.dp))

@Composable
fun PhotosightTheme(content: @Composable () -> Unit) {
    SystemAppearance(isDark = true)
    MaterialTheme(colorScheme = DarkColorScheme, shapes = Shapes, content = content)
}

@Composable
fun TranslucentTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = TranslucentColorScheme, shapes = Shapes, content = content)
}
