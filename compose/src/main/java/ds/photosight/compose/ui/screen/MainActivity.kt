package ds.photosight.compose.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.compose.R
import ds.photosight.compose.repo.PhotosightRepo
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var photosightRepo: PhotosightRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        // clean up splash logo
        setTheme(R.style.Theme_Photosight)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        setContent {
            ComposeApp()
        }
    }
}
