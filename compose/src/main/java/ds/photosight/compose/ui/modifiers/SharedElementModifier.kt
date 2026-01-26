package ds.photosight.compose.ui.modifiers

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalInspectionMode
import ds.photosight.compose.ui.screen.LocalAnimatedVisibilityScope
import ds.photosight.compose.ui.screen.LocalSharedTransitionScope

/**
 * Helper modifier that applies shared bounds animation using CompositionLocal scopes.
 * This eliminates the need to manually pass SharedTransitionScope and AnimatedVisibilityScope.
 *
 * The scopes are provided by Navigation 3's SharedTransitionLayout setup.
 *
 * @param key Unique key to match shared elements between screens (e.g., "photo_123")
 * @param durationMs Animation duration in milliseconds (default: 300ms to match legacy app)
 * @return Modifier with shared bounds applied
 */
fun Modifier.sharedBounds(
    key: Any,
    durationMs: Int = 300,
): Modifier = composed {
    // Skip shared elements in preview mode to prevent crashes
    if (LocalInspectionMode.current) {
        return@composed this
    }

    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            this@composed.sharedBounds(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = BoundsTransform { _, _ ->
                    // Match legacy app timing: 300ms with FastOutSlowInEasing
                    tween(
                        durationMillis = durationMs,
                        easing = FastOutSlowInEasing
                    )
                },
                enter = EnterTransition.None,
                exit = ExitTransition.None
            )
        }
    } else {
        this@composed
    }
}
