package ds.photosight.compose.ui.screen.gallery

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainToolbar(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    onShowAboutDialog: () -> Unit
) {
    TopAppBar(
        contentPadding = WindowInsets.statusBars.asPaddingValues(),
        modifier = modifier
    ) {
        Row {
            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)) {
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