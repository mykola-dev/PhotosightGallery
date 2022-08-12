package ds.photosight.compose.ui.screen.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ds.photosight.compose.ui.theme.Palette

@Composable
fun Drawer() {
    Column(Modifier.background(Palette.translucent)) {
        Ratings()
        Text("the menu")
    }
}

@Composable
fun Ratings() {

}