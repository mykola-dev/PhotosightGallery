package ds.photosight.shared.ui.screen.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Common dispatcher for back press events on desktop.
 */
class BackPressDispatcher {
    private val callbacks = mutableListOf<() -> Unit>()

    fun register(callback: () -> Unit) {
        callbacks.add(callback)
    }

    fun unregister(callback: () -> Unit) {
        callbacks.remove(callback)
    }

    fun onPress(): Boolean {
        if (callbacks.isNotEmpty()) {
            callbacks.last().invoke()
            return true
        }
        return false
    }
}

val LocalBackPressDispatcher = staticCompositionLocalOf { BackPressDispatcher() }

/**
 * Desktop/non-Android implementation of BackHandler.
 * Registers a callback with the global BackPressDispatcher.
 */
@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    val dispatcher = LocalBackPressDispatcher.current
    val currentOnBack by rememberUpdatedState(onBack)

    DisposableEffect(dispatcher, enabled) {
        val callback = { currentOnBack() }
        if (enabled) {
            dispatcher.register(callback)
        }
        onDispose {
            dispatcher.unregister(callback)
        }
    }
}
