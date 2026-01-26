package ds.photosight.compose.ui.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import kotlin.math.abs

fun Modifier.zoomable(imageScale: Float, onClicked: (() -> Unit)? = null): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var scale by remember { mutableStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }
    val angleAnimated by animateFloatAsState(angle)
    val scaleAnimated by animateFloatAsState(scale)
    val panAnimated by animateOffsetAsState(pan)

    val isTransformInProgress = remember { mutableStateOf(false) }

    fun fitScreen(offset: Offset) {
        val screenScale = size.width / size.height.toFloat()
        val shouldFitVertically = screenScale < imageScale
        val (imageWidth: Float, imageHeight: Float) = if (shouldFitVertically) {
            size.width.toFloat() to size.width / imageScale
        } else {
            size.height * imageScale to size.height.toFloat()
        }

        val targetZoom = if (shouldFitVertically) {
            imageScale / screenScale
        } else {
            screenScale / imageScale
        }

        val maxOffsetX = ((imageWidth * targetZoom - size.width) / 2).coerceAtLeast(0f)
        val maxOffsetY = ((imageHeight * targetZoom - size.height) / 2).coerceAtLeast(0f)
        val panX = ((size.width / 2 - offset.x) * targetZoom).coerceIn(-maxOffsetX, maxOffsetX)
        val panY = ((size.height / 2 - offset.y) * targetZoom).coerceIn(-maxOffsetY, maxOffsetY)
        val targetPan = Offset(panX, panY)

        scale = if (abs(targetZoom - scale) > 0.1) targetZoom else 1f
        pan = targetPan
    }

    val idle by remember {
        derivedStateOf {
            isTransformInProgress.value.not()
        }
    }

    if (idle) {
        if (scale < 1.1) {
            pan = Offset.Zero
        }
        angle = 0f
        scale = scale.coerceAtLeast(1f)
    }

    this
        .onGloballyPositioned {
            size = it.size
        }
        .pointerInput(Unit) {
            // Manual implementation to handle consumption precisely
            awaitEachGesture {
                do {
                    val event = awaitPointerEvent()
                    val zoomChange = event.calculateZoom()
                    val panChange = event.calculatePan()
                    val rotationChange = event.calculateRotation()

                    if (scale > 1f || zoomChange != 1f) {
                        isTransformInProgress.value = true
                        // Consuming all changes if we are zoomed or zooming
                        event.changes.forEach { it.consume() }
                        
                        scale = (scale * zoomChange).coerceIn(1f, 10f)
                        pan += panChange
                        angle += rotationChange
                    } else if (event.changes.size > 1) {
                        isTransformInProgress.value = true
                        // Multi-finger gesture, consume it to prevent Pager from weird behavior
                        event.changes.forEach { it.consume() }
                    } else {
                        isTransformInProgress.value = false
                    }
                    // If scale is 1f and it's a single finger move, we don't consume, 
                    // allowing HorizontalPager to catch it.
                } while (event.changes.any { it.pressed })
                isTransformInProgress.value = false
            }
        }
        .graphicsLayer {
            scaleX = scaleAnimated
            scaleY = scaleAnimated
            translationX = panAnimated.x
            translationY = panAnimated.y
            rotationZ = angleAnimated
        }
        .pointerInput(onClicked) {
            detectTapGestures(
                onDoubleTap = {
                    fitScreen(it)
                },
                onTap = {
                    onClicked?.invoke()
                }
            )
        }
}
