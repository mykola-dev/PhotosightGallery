package ds.photosight.shared.ui.screen.viewer

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import ds.photosight.shared.core.widget.zoomable
import ds.photosight.shared.ui.image.buildPhotoImageRequest
import ds.photosight.shared.ui.model.Photo
import ds.photosight.shared.ui.modifiers.sharedBounds
import ds.photosight.shared.util.log

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ZoomableImage(photo: Photo, onClicked: () -> Unit) {
    log.d("ZoomableImage: rendering photo ${photo.id}, large=${photo.large}, thumb=${photo.thumb}")
    SubcomposeAsyncImage(
        model = buildPhotoImageRequest(
            url = photo.large,
            cacheKey = photo.thumb,
            crossfade = true
        ),
        contentDescription = photo.title,
        modifier = Modifier
            .fillMaxSize()
            .sharedBounds(key = photo.transitionKey)
    ) {
        // In Coil3, painter.state is a StateFlow, need to collect it
        val painterState by painter.state.collectAsState()
        log.d("ZoomableImage: painter state = $painterState")
        when (val state = painterState) {
            is AsyncImagePainter.State.Loading -> {
                log.d("ZoomableImage: Loading state - showing placeholder")
                // Show the image content (with thumbnail placeholder) even while loading
                // This is crucial for shared element transition to work smoothly
                SubcomposeAsyncImageContent(
                    modifier = Modifier.fillMaxSize()
                )
            }
            is AsyncImagePainter.State.Success -> {
                log.d("ZoomableImage: Success state")
                val scale = painter.intrinsicSize.run { width / height }
                SubcomposeAsyncImageContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .zoomable(scale) { onClicked() }
                )
            }
            is AsyncImagePainter.State.Error -> {
                log.e("error loading image: url=${photo.large}, error=${state.result.throwable.message}")
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Image(Icons.Default.Close, contentDescription = "Error loading image")
                }
            }
            is AsyncImagePainter.State.Empty -> {
                log.d("ZoomableImage: Empty state")
                Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
