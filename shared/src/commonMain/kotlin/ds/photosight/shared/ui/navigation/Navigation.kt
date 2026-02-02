@file:OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)

package ds.photosight.shared.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey
import ds.photosight.shared.ui.modifiers.LocalAnimatedVisibilityScope
import ds.photosight.shared.ui.modifiers.LocalSharedTransitionScope
import ds.photosight.shared.ui.screen.MainViewModel
import ds.photosight.shared.ui.screen.gallery.GalleryScreen
import ds.photosight.shared.ui.screen.viewer.ViewerScreen
import ds.photosight.shared.ui.theme.PhotosightTheme
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

/** Navigation 3 with SharedTransitionLayout for shared element transitions */
@Composable
fun SharedApp() {
    val mainViewModel: MainViewModel = koinViewModel()

    // Developer-owned back stack, defaulting to GalleryRoute
    val backStack = remember { mutableStateListOf<NavKey>(GalleryRoute) }

    val gridState = rememberLazyStaggeredGridState()

    PhotosightTheme {
        SharedTransitionLayout {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                val selectedKey = backStack.lastOrNull()
                AnimatedContent<Any?>(
                    targetState = selectedKey,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                    label = "AppNavigation"
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
