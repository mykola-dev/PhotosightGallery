package ds.photosight.compose.ui.screen.viewer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            AnimatedContent(
                targetState = title to subtitle,
                transitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform() }
            ) { content ->
                Column(modifier = Modifier
                    .padding(16.dp)
                    .statusBarsPadding()
                    .fillMaxWidth()) {
                    Text(
                        content.first,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                    Text(
                        content.second,
                        fontSize = 12.sp,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }
    }
}
