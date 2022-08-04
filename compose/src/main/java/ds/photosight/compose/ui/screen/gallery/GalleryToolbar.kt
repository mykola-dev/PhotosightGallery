package ds.photosight.compose.ui.screen.gallery

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ds.photosight.compose.ui.theme.PhotosightTheme

@Composable
fun MainToolbar(
    titleState: State<String>,
    subtitle: String?,
    modifier: Modifier = Modifier,
    onShowAboutDialog: () -> Unit
) {
    TopAppBar(
        contentPadding = WindowInsets.statusBars.asPaddingValues(),
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                //verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
                    .animateContentSize()
            ) {
                val title by titleState
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                if (subtitle != null) {
                    Text(subtitle, fontSize = 12.sp)
                }
            }
            IconButton(onClick = onShowAboutDialog) {
                Icon(Icons.Filled.Info, null)
            }
        }
    }
}

@Preview
@Composable
fun ToolbarPreview() {
    PhotosightTheme {
        /*MainToolbar(title = "hello", subtitle = null) {

        }*/
    }
}

