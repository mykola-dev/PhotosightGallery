package ds.photosight.compose.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ds.photosight.compose.R
import ds.photosight.shared.ui.navigation.SharedApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Photosight)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            SharedApp()
        }
    }
}
