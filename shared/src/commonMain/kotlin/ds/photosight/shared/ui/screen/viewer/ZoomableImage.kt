package ds.photosight.shared.ui.screen.viewer

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import ds.photosight.shared.ui.model.Photo
import ds.photosight.shared.ui.modifiers.sharedBounds
import ds.photosight.shared.core.widget.zoomable
import ds.photosight.shared.ui.image.buildPhotoImageRequest
import ds.photosight.shared.util.log

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ZoomableImage(photo: Photo, onClicked: () -> Unit) {
    SubcomposeAsyncImage(
        model = buildPhotoImageRequest(
            url = photo.large,
            cacheKey = photo.thumb,
            crossfade = true
        ),
        contentDescription = photo.title,
        modifier = Modifier
            .fillMaxSize()
            // TODO: Re-enable shared element transitions once API is fixed
            // .sharedBounds(key = photo.transitionKey)
    ) {
        when (val state = painter.state) {
            is AsyncImagePainter.State.Loading, is AsyncImagePainter.State.Success -> {
                val scale = if (state is AsyncImagePainter.State.Success) {
                    painter.intrinsicSize.run { width / height }
                } else {
                    1f
                }

                SubcomposeAsyncImageContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .zoomable(scale) { onClicked() }
                )
            }
            is AsyncImagePainter.State.Error -> {
                log.e("error loading image")
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Image(Icons.Default.Close, null)
                }
            }
            else -> {}
        }
    }
}
