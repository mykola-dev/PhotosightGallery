package ds.photosight.compose.ui.screen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ds.photosight.compose.ui.screen.gallery.GalleryScreen
import ds.photosight.compose.ui.screen.viewer.ViewerScreen
import ds.photosight.compose.ui.theme.PhotosightTheme
import org.koin.androidx.compose.koinViewModel

// CompositionLocal to provide SharedTransitionScope to destinations
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

// CompositionLocal to provide AnimatedVisibilityScope to destination content
val LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> {
    null
}

/**
 * Navigation 2 with SharedTransitionLayout for shared element transitions
 */
@Composable
fun ComposeApp() {
    val mainViewModel: MainViewModel = koinViewModel()
    val navController = rememberNavController()

    PhotosightTheme {
        SharedTransitionLayout {
            // Provide the SharedTransitionScope to all destinations
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                NavHost(
                    navController = navController,
                    startDestination = "gallery",
                    enterTransition = { fadeIn(tween(300)) },
                    exitTransition = { fadeOut(tween(200)) },
                    popEnterTransition = { fadeIn(tween(200)) },
                    popExitTransition = { fadeOut(tween(300)) }
                ) {
                    // Gallery Screen
                    composable("gallery") {
                        // Provide AnimatedVisibilityScope to children
                        val scope = this@composable
                        CompositionLocalProvider(LocalAnimatedVisibilityScope provides scope) {
                            GalleryScreen(
                                mainViewModel = mainViewModel,
                                onNavigateToViewer = { photoId, index ->
                                    navController.navigate("viewer/$photoId/$index")
                                }
                            )
                        }
                    }

                    // Viewer Screen
                    composable(
                        route = "viewer/{photoId}/{index}",
                        arguments = listOf(
                            androidx.navigation.navArgument("photoId") { type = NavType.IntType },
                            androidx.navigation.navArgument("index") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val photoId = backStackEntry.arguments?.getInt("photoId") ?: return@composable
                        val index = backStackEntry.arguments?.getInt("index") ?: 0
                        // Provide AnimatedVisibilityScope to children
                        val scope = this@composable
                        CompositionLocalProvider(LocalAnimatedVisibilityScope provides scope) {
                            ViewerScreen(
                                mainViewModel = mainViewModel,
                                photoId = photoId,
                                index = index,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
