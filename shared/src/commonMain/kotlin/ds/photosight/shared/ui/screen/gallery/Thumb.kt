package ds.photosight.shared.ui.screen.gallery

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import ds.photosight.shared.ui.model.Photo
import ds.photosight.shared.ui.modifiers.sharedBounds
import ds.photosight.shared.ui.image.buildPhotoImageRequest
import ds.photosight.shared.ui.theme.Palette
import kotlinx.coroutines.delay

@Composable
fun Thumb(
    item: Photo,
    onPhotoClicked: (Photo) -> Unit,
    isPreloading: Boolean = false,
) {
    val url = item.thumb

    var isBadImage by remember(url) { mutableStateOf(false) }
    var retryHash by remember { mutableIntStateOf(0) }

    // Auto-retry loop for bad icons
    LaunchedEffect(isBadImage) {
        if (isBadImage) {
            delay(if (retryHash < 3) 2000L else 5000L)
            retryHash++
            isBadImage = false
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        SubcomposeAsyncImage(
            model = buildPhotoImageRequest(
                url = if (retryHash == 0) url else "$url?retry=$retryHash",
                cacheKey = item.cacheKey,
                crossfade = true
            ),
            contentDescription = item.title,
            contentScale = ContentScale.FillWidth,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(1.dp)
                    .sharedBounds(key = item.transitionKey)
                    .clickable(enabled = !isPreloading) { onPhotoClicked(item) }
        ) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Loading || isBadImage) {
                ThumbLoadingAnimation()
            } else if (state is AsyncImagePainter.State.Error) {
                // Simple placeholder for error state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Palette.greyDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error", color = Palette.primary)
                }
            } else {
                SubcomposeAsyncImageContent()
            }
        }

        // Show loading overlay when preloading full-size image
        if (isPreloading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {                 CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary) }
        }
    }
}

@Composable
private fun ThumbLoadingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "thumb_loading")
    val color by
    infiniteTransition.animateColor(
        initialValue = Palette.greyDark,
        targetValue = Palette.grey,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
        label = "color"
    )

    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .drawBehind { drawRect(color) })
}
