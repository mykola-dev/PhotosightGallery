package ds.photosight.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.compose.repo.PhotosightRepo
import ds.photosight.compose.ui.screen.navigation.ComposeApp
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var photosightRepo: PhotosightRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

/*        lifecycleScope.launch {
            photosightRepo.getCategories()
        }*/

        // This app draws behind the system bars, so we want to handle fitting system windows
        //WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
           ComposeApp()
        }
    }
}
