package ds.photosight.compose.ui.screen.viewer

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.memory.MemoryCache
import ds.photosight.compose.ui.model.Photo
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
        if (placeholder != null && showPlaceHolder) {
            Image(placeholder, contentDescription = null, modifier = Modifier.fillMaxSize())
        }

        when (state) {
            is AsyncImagePainter.State.Error -> {
                log.e("error")
                Image(Icons.Default.Close, null)
            }
            else -> {}
        }

        AnimatedVisibility(state is AsyncImagePainter.State.Success, enter = fadeIn()) {
            showPlaceHolder = this.transition.currentState != EnterExitState.Visible

            state as AsyncImagePainter.State.Success
            val scale = remember {
                painter.intrinsicSize.run {
                    width / height
                }
            }

            //log.v("photo=$photo")
            //log.v("measured scale $scale")
            SubcomposeAsyncImageContent(
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(scale) { onClicked() }
            )
        }

        AnimatedVisibility(state is AsyncImagePainter.State.Loading, enter = EnterTransition.None, exit = fadeOut()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
    }
}