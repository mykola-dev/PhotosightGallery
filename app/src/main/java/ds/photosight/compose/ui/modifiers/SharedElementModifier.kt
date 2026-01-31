package ds.photosight.compose.ui.modifiers

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalInspectionMode
import ds.photosight.compose.ui.navigation.LocalAnimatedVisibilityScope
import ds.photosight.compose.ui.navigation.LocalSharedTransitionScope

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
): Modifier = composed {
    // Skip shared elements in preview mode to prevent crashes
    if (LocalInspectionMode.current) {
        return@composed this
    }

    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            this@composed.sharedElement(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = { _, _ ->
                    // Entry: 400ms (as requested by user for luxurious feel)
                    // Exit: 300ms
                    val isExiting = animatedVisibilityScope.transition.targetState == EnterExitState.PostExit
                    val finalDuration = if (isExiting) 300 else 400
                    
                    tween(
                        durationMillis = finalDuration,
                        easing = FastOutSlowInEasing
                    )
                }
            )
        }
    } else {
        this@composed
    }
}
