package ds.photosight.compose.ui.screen.viewer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ViewerToolbar(isVisible: Boolean, title: String, subtitle: String) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(8.dp)
        ) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 12.sp)
        }
    }
}