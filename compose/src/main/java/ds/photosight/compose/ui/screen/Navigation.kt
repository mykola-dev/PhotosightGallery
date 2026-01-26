package ds.photosight.compose.ui.screen

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import ds.photosight.compose.ui.screen.gallery.GalleryScreen
import ds.photosight.compose.ui.screen.viewer.ViewerScreen
import ds.photosight.compose.ui.theme.PhotosightTheme

// CompositionLocal to provide SharedTransitionScope to destinations
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

// CompositionLocal to provide AnimatedVisibilityScope to destination content
val LocalAnimatedVisibilityScope = compositionLocalOf<androidx.compose.animation.AnimatedVisibilityScope?> {
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
                    startDestination = "gallery"
                ) {
                    // Gallery Screen
                    composable("gallery") {
                        // Provide AnimatedVisibilityScope to children
                        val scope = this@composable
                        CompositionLocalProvider(LocalAnimatedVisibilityScope provides scope) {
                            GalleryScreen(
                                mainViewModel = mainViewModel,
                                onNavigateToViewer = { photoId ->
                                    navController.navigate("viewer/$photoId")
                                }
                            )
                        }
                    }

                    // Viewer Screen
                    composable(
                        route = "viewer/{photoId}",
                        arguments = listOf(
                            androidx.navigation.navArgument("photoId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val photoId = backStackEntry.arguments?.getInt("photoId") ?: return@composable
                        // Provide AnimatedVisibilityScope to children
                        val scope = this@composable
                        CompositionLocalProvider(LocalAnimatedVisibilityScope provides scope) {
                            ViewerScreen(
                                mainViewModel = mainViewModel,
                                photoId = photoId,
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
