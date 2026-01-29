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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
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

    Box(modifier = Modifier.fillMaxWidth()) {
        SubcomposeAsyncImage(
                model =
                        ImageRequest.Builder(LocalContext.current)
                                .data(url)
                                .placeholderMemoryCacheKey(item.cacheKey)
                                .crossfade(200)
                                .build(),
                loading = {
                    val infiniteTransition = rememberInfiniteTransition()
                    val color by
                            infiniteTransition.animateColor(
                                    initialValue = Palette.greyDark,
                                    targetValue = Palette.grey,
                                    animationSpec =
                                            infiniteRepeatable(
                                                    animation = tween(2000, easing = LinearEasing),
                                                    repeatMode = RepeatMode.Reverse
                                            )
                            )

                    Box(Modifier.fillMaxWidth().aspectRatio(1f).drawBehind { drawRect(color) })
                },
                error = { Image(painterResource(id = R.drawable.photo_placeholder_vector), null) },
                contentDescription = item.title,
                contentScale = ContentScale.FillWidth,
                modifier =
                        Modifier.fillMaxWidth()
                                .padding(1.dp)
                                .sharedBounds(
                                        key = item.transitionKey
                                ) // Shared element transition!
                                .clickable(enabled = !isPreloading) { onPhotoClicked(item) }
        )

        // Show loading overlay when preloading full-size image
        if (isPreloading) {
            Box(
                    modifier =
                            Modifier.matchParentSize() // Match the SubcomposeAsyncImage size
                                    // exactly
                                    .padding(1.dp),
                    contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary) }
        }
    }
}
