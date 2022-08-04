package ds.photosight.compose.ui.screen.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import ds.photosight.compose.ui.DebugView
import ds.photosight.compose.ui.screen.gallery.GalleryScreen
import ds.photosight.compose.ui.theme.PhotosightTheme

@Composable
fun ComposeApp() {

    val mainViewModel: MainViewModel = viewModel()

    // todo navigation
    PhotosightTheme {
        GalleryScreen(mainViewModel)
        //DebugView()
    }
}