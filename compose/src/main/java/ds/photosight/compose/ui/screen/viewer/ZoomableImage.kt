package ds.photosight.compose.ui.screen.viewer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.memory.MemoryCache
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.modifiers.sharedBounds
import ds.photosight.compose.ui.widget.zoomable
import ds.photosight.compose.util.log

@Composable
fun ZoomableImage(photo: Photo, onClicked: () -> Unit) {
    SubcomposeAsyncImage(
        model = photo.large,
        contentDescription = photo.title,
    ) {
        val state = painter.state

        var showPlaceHolder by remember { mutableStateOf(true) }
        val cacheKey = MemoryCache.Key(photo.thumb)
        val placeholder = remember {
            painter
                .imageLoader
                .memoryCache
                ?.get(cacheKey)
                ?.bitmap
                ?.asImageBitmap()
        }

        // Check if full-size image is already in memory cache (from preloading)
        val fullImageCached = remember(state) {
            painter.imageLoader.memoryCache?.get(MemoryCache.Key(photo.large)) != null
        }

        // Don't show placeholder if full image is cached (transition scenario)
        if (placeholder != null && showPlaceHolder && !fullImageCached) {
            Image(placeholder, contentDescription = null, modifier = Modifier.fillMaxSize())
        }

        when (state) {
            is AsyncImagePainter.State.Error -> {
                log.e("error")
                Image(Icons.Default.Close, null)
            }
            else -> {}
        }

        // If image is cached, skip the animation for immediate display
        val enterTransition = if (fullImageCached) EnterTransition.None else fadeIn()
        AnimatedVisibility(state is AsyncImagePainter.State.Success, enter = enterTransition) {
            state as AsyncImagePainter.State.Success
            val scale = remember {
                painter.intrinsicSize.run {
                    width / height
                }
            }

            SubcomposeAsyncImageContent(
                modifier = Modifier
                    .fillMaxSize()
                    .sharedBounds(key = photo.transitionKey) // Shared element transition!
                    .zoomable(scale) { onClicked() }
            )
        }

        // Only show loading if not cached
        AnimatedVisibility(state is AsyncImagePainter.State.Loading && !fullImageCached, enter = EnterTransition.None, exit = fadeOut()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
    }
}
