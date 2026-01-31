package ds.photosight.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp

@Composable
inline fun <T> rememberDerived(key: Any? = null, noinline calculation: () -> T) = remember(key) {
    derivedStateOf(calculation)
}

@Composable
fun Dp.toPx(): Float = with(LocalDensity.current) { toPx() }

@Composable
fun Dp.roundToPx(): Int = with(LocalDensity.current) { roundToPx() }


val isPreview: Boolean @Composable get() = LocalInspectionMode.current