package ds.photosight.shared.core.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.math.max

/**
 * RGB histogram data containing color distribution arrays.
 * Each array contains 256 values representing pixel count at each intensity level.
 */
class HistogramData(
    val r: IntArray,
    val g: IntArray,
    val b: IntArray,
)

/**
 * Displays an RGB histogram as three overlapping curves (Red, Green, Blue).
 * The histogram shows the color intensity distribution of the image.
 */
@Composable
fun Histogram(data: HistogramData?) {
    Canvas(
        Modifier
            .aspectRatio(2f)
            .fillMaxSize()
    ) {
        if (data == null) return@Canvas
        
        val hd = data
        val max = listOf(hd.r.max(), hd.g.max(), hd.b.max()).max()
        val minFactor = size.height / 12000
        val xFactor = size.width / 256
        val yFactor = max(size.height / max.toFloat(), minFactor)

        val pathR = Path()
        val pathG = Path()
        val pathB = Path()

        pathR.moveTo(0f, size.height)
        pathG.moveTo(0f, size.height)
        pathB.moveTo(0f, size.height)

        for (i in 0 until 255) {
            val x1 = i * xFactor
            var y1 = size.height - hd.r[i] * yFactor
            val x2 = (i + 1) * xFactor
            var y2 = size.height - hd.r[i + 1] * yFactor

            pathR.quadraticTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2)

            y1 = size.height - hd.g[i] * yFactor
            y2 = size.height - hd.g[i + 1] * yFactor
            pathG.quadraticTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2)

            y1 = size.height - hd.b[i] * yFactor
            y2 = size.height - hd.b[i + 1] * yFactor
            pathB.quadraticTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2)
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
