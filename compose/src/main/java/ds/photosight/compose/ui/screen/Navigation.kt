package ds.photosight.compose.ui.screen

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import ds.photosight.compose.ui.NavGraphs
import ds.photosight.compose.ui.theme.PhotosightTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ComposeApp() {

    val mainViewModel: MainViewModel = koinViewModel()

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