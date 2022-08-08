package ds.photosight.compose

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.compose.repo.PhotosightRepo
import ds.photosight.compose.ui.screen.navigation.ComposeApp
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var photosightRepo: PhotosightRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        // clean up splash logo
        setTheme(R.style.Theme_Photosight)
        super.onCreate(savedInstanceState)


        setContent {
            ComposeApp()
        }
    }
}
