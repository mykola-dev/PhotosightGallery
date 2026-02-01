@file:OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)

package ds.photosight.shared.ui.navigation

import androidx.compose.animation.AnimatedContent
import kotlinx.serialization.Serializable
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.NavKey
import ds.photosight.shared.ui.screen.MainViewModel
import ds.photosight.shared.ui.screen.gallery.GalleryScreen
import ds.photosight.shared.ui.screen.viewer.ViewerScreen
import ds.photosight.shared.ui.theme.PhotosightTheme
import org.koin.compose.viewmodel.koinViewModel

import ds.photosight.shared.ui.modifiers.LocalAnimatedVisibilityScope
import ds.photosight.shared.ui.modifiers.LocalSharedTransitionScope

/** Navigation 3 with SharedTransitionLayout for shared element transitions */
@Composable
fun SharedApp() {
    val mainViewModel: MainViewModel = koinViewModel()

    // Developer-owned back stack, defaulting to GalleryRoute
    val backStack = rememberNavBackStack(GalleryRoute)

    val gridState = rememberLazyStaggeredGridState()

    PhotosightTheme {
        SharedTransitionLayout {
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
                                        gridState = gridState,
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

// Routes for Navigation 3
@Serializable
data object GalleryRoute : NavKey

@Serializable
data class ViewerRoute(val photoId: Int, val index: Int) : NavKey
