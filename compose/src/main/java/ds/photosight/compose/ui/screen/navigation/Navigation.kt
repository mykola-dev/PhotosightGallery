package ds.photosight.compose.ui.screen.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import ds.photosight.compose.ui.DebugView
import ds.photosight.compose.ui.NavGraphs
import ds.photosight.compose.ui.theme.PhotosightTheme

@Composable
fun ComposeApp() {

    val mainViewModel: MainViewModel = hiltViewModel()

    PhotosightTheme {
        //DebugView()
        DestinationsNavHost(
            navGraph = NavGraphs.root,
            dependenciesContainerBuilder = {
                dependency(mainViewModel)
            }
        )
    }
}