package ds.photosight.compose.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import ds.photosight.compose.ui.screen.gallery.GalleryScreen
import ds.photosight.compose.ui.screen.viewer.ViewerScreen
import ds.photosight.compose.ui.theme.PhotosightTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

// Navigation 3 routes
@Serializable data object GalleryRoute : NavKey

@Serializable data class ViewerRoute(val photoId: Int, val index: Int) : NavKey

// CompositionLocal to provide SharedTransitionScope to destinations
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

// CompositionLocal to provide AnimatedVisibilityScope to destination content
val LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

/** Navigation 3 with SharedTransitionLayout for shared element transitions */
@Composable
fun ComposeApp() {
    val mainViewModel: MainViewModel = koinViewModel()

    // Developer-owned back stack, defaulting to GalleryRoute
    val backStack = rememberNavBackStack(GalleryRoute)

    PhotosightTheme {
        SharedTransitionLayout {
            // Provide the SharedTransitionScope to all destinations
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                val selectedKey = backStack.lastOrNull()
                AnimatedContent(
                        targetState = selectedKey,
                        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                ) { key ->
                    if (key != null) {
                        CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                            when (key) {
                                is GalleryRoute -> {
                                    GalleryScreen(
                                            mainViewModel = mainViewModel,
                                            onNavigateToViewer = { photoId, index ->
                                                // Navigate by adding to the back stack
                                                backStack.add(ViewerRoute(photoId, index))
                                            }
                                    )
                                }
                                is ViewerRoute -> {
                                    ViewerScreen(
                                            mainViewModel = mainViewModel,
                                            photoId = key.photoId,
                                            index = key.index,
                                            onBack = {
                                                // Pop by removing the last item
                                                backStack.removeAt(backStack.lastIndex)
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
