package ds.photosight.shared.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HistogramUtilsTest {

    @Test
    fun testHistogramCalculation() {
        // Create a simple 2x2 red ImageBitmap
        val bitmap = ImageBitmap(2, 2)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { color = Color.Red }
        canvas.drawRect(0f, 0f, 2f, 2f, paint)

        val data = computeHistogram(bitmap)
        
        assertNotNull(data)
        // 4 pixels, all red
        assertEquals(4, data.r[255])
        assertEquals(4, data.g[0])
        assertEquals(4, data.b[0])
        
        // Check other bins are 0
        assertEquals(0, data.r[0])
        assertEquals(0, data.g[255])
        assertEquals(0, data.b[255])
    }
}
