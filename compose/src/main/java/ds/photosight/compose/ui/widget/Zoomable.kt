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
import ds.photosight.compose.util.log
import kotlin.math.abs

fun Modifier.zoomable(imageScale: Float): Modifier = composed {

    var size by remember { mutableStateOf(IntSize.Zero) }
    var scale by remember { mutableStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }
    val angleAnimated by animateFloatAsState(angle)
    val scaleAnimated by animateFloatAsState(scale)
    val panAnimated by animateOffsetAsState(pan)

    val transformableState = rememberTransformableState { z, p, r ->
        log.v("zoom=$z pan=$p rotate=$r")
        scale += z - 1f
        pan += p
        angle += r
    }

    fun fitScreen() {
        val screenScale = size.width / size.height.toFloat()
        val targetZoom = if (screenScale > 1f) {
            screenScale / imageScale  // landscape
        } else {
            imageScale / screenScale // portrait
        }
        log.v("fit: screenScale=$screenScale imageScale=$imageScale targetScale=$targetZoom scale=$scale")
        scale = if (abs(targetZoom - scale) > 0.1) targetZoom else 1f
        pan = Offset.Zero
    }

    val idle by derivedStateOf {
        transformableState.isTransformInProgress.not() && angle != 0f
    }

    if (idle) {
        log.v("idle")
        angle = 0f
        scale = scale.coerceAtLeast(1f)
    }

    this
        .onGloballyPositioned {
            size = it.size
            log.v("global size=${it.size}")
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
                    log.v("double tap")
                    fitScreen()
                },
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