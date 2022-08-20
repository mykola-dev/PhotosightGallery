package ds.photosight.compose.ui.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.*
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

    val transformableState = rememberTransformableState { z, p, r ->
        //log.v("zoom=$z pan=$p rotate=$r")
        scale *= z
        pan += p
        angle += r
    }

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

        //log.v("screenScale=$screenScale imageScale=$imageScale zoom=$targetZoom maxPan=${maxOffsetX}x${maxOffsetY} panX=$panX size=${imageWidth}x${imageHeight}")
    }

    val idle by derivedStateOf {
        transformableState.isTransformInProgress.not() //&& angle != 0f
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
        .transformable(
            state = transformableState,
            lockRotationOnZoomPan = false,
            enabled = true
        )
        .graphicsLayer {
            scaleX = scaleAnimated
            scaleY = scaleAnimated
            translationX = panAnimated.x
            translationY = panAnimated.y
            rotationZ = angleAnimated
        }
        .pointerInput(Unit) {
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


fun Modifier.onPointerUp(block: () -> Unit): Modifier = pointerInput(Unit) {
    forEachGesture {
        awaitPointerEventScope {
            awaitFirstDown()
            waitForUpOrCancellation()
            block()
        }
    }
}