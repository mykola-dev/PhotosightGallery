package ds.photosight.shared.ui.modifiers

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.runtime.Composable
import androidx.compose.animation.EnterExitState

/**
 * Helper modifier that applies shared bounds animation using CompositionLocal scopes.
 * This eliminates the need to manually pass SharedTransitionScope and AnimatedVisibilityScope.
 *
 * The scopes should be provided by Navigation 3's SharedTransitionLayout setup.
 *
 * @param key Unique key to match shared elements between screens (e.g., "photo_123")
 * @return Modifier with shared bounds applied
 */
@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.sharedBounds(
    key: Any,
): Modifier = composed {
    // Skip shared elements in preview mode to prevent crashes
    if (LocalInspectionMode.current) {
        return@composed this
    }

    // For Compose Multiplatform, we'll need to get the scopes from CompositionLocals
    // These will be provided by the navigation setup
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            this@composed.sharedBounds(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else {
        this@composed
    }
}

// CompositionLocals for shared transition scopes (to be provided by navigation setup)
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = androidx.compose.runtime.compositionLocalOf<SharedTransitionScope?> { null }

val LocalAnimatedVisibilityScope = androidx.compose.runtime.compositionLocalOf<androidx.compose.animation.AnimatedVisibilityScope?> { null }
