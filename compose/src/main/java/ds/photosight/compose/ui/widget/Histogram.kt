package ds.photosight.compose.ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.AndroidPath
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import ds.photosight.compose.util.logCompositions
import kotlin.math.max

@Composable
fun Histogram(data: HistogramData?) {
    logCompositions("histogram $data")

    Canvas(
        Modifier
            .aspectRatio(2f)
            .fillMaxSize()
    ) {
        data?.run {

            val max = listOf(r.max(), g.max(), b.max()).max()
            val minFactor = size.height / 12000
            val xFactor = size.width / 256
            val yFactor = max(size.height / max.toFloat(), minFactor)

            val pathR = AndroidPath()
            val pathG = AndroidPath()
            val pathB = AndroidPath()

            pathR.moveTo(0f, size.height)
            pathG.moveTo(0f, size.height)
            pathB.moveTo(0f, size.height)

            for (i in 0 until 255) {

                val x1 = i * xFactor
                var y1 = size.height - r[i] * yFactor
                val x2 = (i + 1) * xFactor
                var y2 = size.height - r[i + 1] * yFactor

                pathR.internalPath.quadTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2)

                y1 = size.height - g[i] * yFactor
                y2 = size.height - g[i + 1] * yFactor
                pathG.internalPath.quadTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2)

                y1 = size.height - b[i] * yFactor
                y2 = size.height - b[i + 1] * yFactor
                pathB.internalPath.quadTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2)

            }
            pathR.lineTo(size.width, size.height)
            pathG.lineTo(size.width, size.height)
            pathB.lineTo(size.width, size.height)

            drawPath(pathR, Color.Red)
            drawPath(pathG, Color.Green)
            drawPath(pathB, Color.Blue)
            drawPath(pathR, Color.Red, blendMode = BlendMode.Screen)
            drawPath(pathG, Color.Green, blendMode = BlendMode.Screen)
            drawPath(pathB, Color.Blue, blendMode = BlendMode.Screen)

        }
    }
}

class HistogramData(
    val r: IntArray,
    val g: IntArray,
    val b: IntArray,
)