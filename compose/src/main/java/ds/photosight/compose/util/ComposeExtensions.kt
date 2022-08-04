package ds.photosight.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

@Composable
inline fun <T> rememberDerived(key: Any? = null, noinline calculation: () -> T) = remember(key) {
    derivedStateOf(calculation)
}
