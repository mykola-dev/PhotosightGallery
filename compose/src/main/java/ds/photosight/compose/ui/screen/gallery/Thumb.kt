package ds.photosight.compose.ui.screen.gallery

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.CachePolicy
import coil.request.ImageRequest
import ds.photosight.compose.R
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.modifiers.sharedBounds
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.util.logCompositions

@Composable
fun Thumb(
        item: Photo,
        onPhotoClicked: (Photo) -> Unit,
        isPreloading: Boolean = false,
) {
        val url = item.thumb
        logCompositions(msg = "paged item ${item.id}")

        var isBadImage by remember(url) { mutableStateOf(false) }
        var retryHash by remember { mutableStateOf(0) }

        // Auto-retry loop for bad icons
        androidx.compose.runtime.LaunchedEffect(isBadImage) {
                if (isBadImage) {
                        kotlinx.coroutines.delay(if (retryHash < 3) 2000L else 5000L)
                        retryHash++
                        isBadImage = false
                }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
                SubcomposeAsyncImage(
                        model =
                                ImageRequest.Builder(LocalContext.current)
                                        .data(if (retryHash == 0) url else "$url?retry=$retryHash")
                                        .placeholderMemoryCacheKey(item.cacheKey)
                                        .crossfade(200)
                                        .diskCachePolicy(
                                                if (retryHash > 0) CachePolicy.DISABLED
                                                else CachePolicy.ENABLED
                                        )
                                        .listener(
                                                onSuccess = { _, result ->
                                                        val width = result.drawable.intrinsicWidth
                                                        val height = result.drawable.intrinsicHeight
                                                        // Detect "little icons" (usually < 100px)
                                                        if (width > 0 &&
                                                                        width < 100 &&
                                                                        height > 0 &&
                                                                        height < 100
                                                        ) {
                                                                isBadImage = true
                                                        } else {
                                                                isBadImage = false
                                                        }
                                                },
                                                onError = { _, _ ->
                                                        // Fallback retry for network errors too
                                                        isBadImage = true
                                                }
                                        )
                                        .build(),
                        contentDescription = item.title,
                        contentScale = ContentScale.FillWidth,
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(1.dp)
                                        .sharedBounds(
                                                key = item.transitionKey
                                        ) // Shared element transition!
                                        .clickable(enabled = !isPreloading) { onPhotoClicked(item) }
                ) {
                        val state = painter.state
                        if (state is coil.compose.AsyncImagePainter.State.Loading || isBadImage) {
                                ThumbLoadingAnimation()
                        } else if (state is coil.compose.AsyncImagePainter.State.Error) {
                                Image(
                                        painterResource(id = R.drawable.photo_placeholder_vector),
                                        null,
                                        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                                )
                        } else {
                                SubcomposeAsyncImageContent()
                        }
                }

                // Show loading overlay when preloading full-size image
                if (isPreloading) {
                        Box(
                                modifier =
                                        Modifier.matchParentSize() // Match the SubcomposeAsyncImage
                                                // size
                                                // exactly
                                                .padding(1.dp),
                                contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary) }
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

        Box(Modifier.fillMaxWidth().aspectRatio(1f).drawBehind { drawRect(color) })
}
