package ds.photosight.shared.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import ds.photosight.http_client.downloadBytes
import ds.photosight.shared.core.widget.HistogramData
import org.jetbrains.compose.resources.decodeToImageBitmap

/**
 * Loads histogram data from an image URL.
 */
suspend fun loadHistogramData(url: String): HistogramData? {
    return try {
        val bytes = downloadBytes(url)
        decodeAndComputeHistogram(bytes)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Decodes image bytes and computes histogram data.
 */
fun decodeAndComputeHistogram(bytes: ByteArray): HistogramData? {
    return try {
        val bitmap = decodeImage(bytes) ?: return null
        computeHistogram(bitmap)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Fully multiplatform image decoding using Compose Resources API.
 */
fun decodeImage(bytes: ByteArray): ImageBitmap? {
    return try {
        bytes.decodeToImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Computes histogram data from an ImageBitmap.
 * This logic is fully multiplatform.
 */
fun computeHistogram(bitmap: ImageBitmap): HistogramData {
    val pixelMap = bitmap.toPixelMap()
    
    val r = IntArray(256)
    val g = IntArray(256)
    val b = IntArray(256)

    for (y in 0 until pixelMap.height) {
        for (x in 0 until pixelMap.width) {
            val color = pixelMap[x, y]
            r[(color.red * 255).toInt()]++
            g[(color.green * 255).toInt()]++
            b[(color.blue * 255).toInt()]++
        }
    }

    return HistogramData(r, g, b)
}
