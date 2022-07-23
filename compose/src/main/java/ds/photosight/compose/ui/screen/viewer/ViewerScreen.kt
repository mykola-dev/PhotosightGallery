package ds.photosight.compose.ui.screen.viewer

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun ViewerScreen() {
    val vm: ViewerViewModel = hiltViewModel()
    ViewerContent()
}

@Composable
fun ViewerContent() {

}