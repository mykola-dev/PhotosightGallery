package ds.photosight.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.compose.repo.PhotosightRepo
import ds.photosight.compose.ui.screen.gallery.GalleryScreen
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.ui.theme.PhotosightTheme
import kotlinx.coroutines.launch
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
            PhotosightTheme {
                GalleryScreen()
            }
        }
    }
}
