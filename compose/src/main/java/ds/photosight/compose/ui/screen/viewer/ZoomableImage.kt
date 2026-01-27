package ds.photosight.compose.ui.screen.viewer

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.modifiers.sharedBounds
import ds.photosight.compose.ui.screen.LocalAnimatedVisibilityScope
import ds.photosight.compose.ui.widget.zoomable
import ds.photosight.compose.util.log

@Composable
fun ZoomableImage(photo: Photo, onClicked: () -> Unit) {
    val context = LocalContext.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    // Coordination: Hide destination until transition completes to prevent doubling
    val isTransitionFinished by remember(animatedVisibilityScope) {
        derivedStateOf {
            val transition = animatedVisibilityScope?.transition
            transition?.currentState == transition?.targetState && transition?.currentState == EnterExitState.Visible
        }
    }

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(photo.large)
            .placeholderMemoryCacheKey(photo.thumb) 
            .crossfade(true) 
            .build(),
        contentDescription = photo.title,
        modifier = Modifier
            .fillMaxSize()
            .sharedBounds(key = photo.transitionKey) 
    ) {
        val state = painter.state

        when (state) {
            is AsyncImagePainter.State.Loading, is AsyncImagePainter.State.Success -> {
                val scale = if (state is AsyncImagePainter.State.Success) {
                    painter.intrinsicSize.run { width / height }
                } else {
                    1f 
                }

                SubcomposeAsyncImageContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (isTransitionFinished) 1f else 0.01f) // Hide during transition
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
