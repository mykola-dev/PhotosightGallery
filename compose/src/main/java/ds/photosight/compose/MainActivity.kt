package ds.photosight.compose

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.compose.repo.PhotosightRepo
import ds.photosight.compose.ui.screen.navigation.ComposeApp
import ds.photosight.compose.util.log
import ds.photosight.http_client.httpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Inet4Address
import java.net.InetAddress
import java.time.Duration
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
