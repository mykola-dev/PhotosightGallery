package ds.photosight.compose.ui.screen.viewer

import android.graphics.Bitmap
import android.graphics.Path
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.AndroidPath
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ds.photosight.compose.R
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.ui.theme.PhotosightTheme
import ds.photosight.compose.ui.widget.LinkifyText
import ds.photosight.compose.util.loadBitmap
import ds.photosight.compose.util.logCompositions
import timber.log.Timber
import kotlin.math.max

@Composable
fun InfoSheet(photo: Photo, visible: Boolean) {
    Column(
        Modifier
            .padding(16.dp)
            .navigationBarsPadding()
    ) {
        Text(stringResource(R.string.photo_details), style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(16.dp))
        InfoRow(stringResource(R.string.title), photo.title)
        InfoRow(stringResource(R.string.author), "${photo.authorName} ${photo.authorUrl}")
        InfoRow(stringResource(R.string.source), photo.pageUrl)
        Spacer(Modifier.height(16.dp))

        var data by remember(photo) { mutableStateOf<HistogramData?>(null) }
        val ctx = LocalContext.current
        LaunchedEffect(photo, visible) {
            if (visible) {
                val bitmap = ctx.loadBitmap(photo.thumb)
                data = process(bitmap)
            }
        }
        Histogram(data)
    }
}

@Composable
fun Histogram(data: HistogramData?) {
    logCompositions("histogram")

    Canvas(
        Modifier
            .aspectRatio(2f)
            .fillMaxSize()
        //.background(Color.LightGray)
    ) {
        data?.run {

            val max = listOf(r.max(), g.max(), b.max()).max()
            val minFactor = size.height / 12000
            val xFactor = size.width / 256
            val yFactor = max(size.height / max.toFloat(), minFactor)

            val pathR = AndroidPath(Path())
            val pathG = AndroidPath(Path())
            val pathB = AndroidPath(Path())

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

@Composable
fun InfoRow(header: String, value: String) {
    val style = MaterialTheme.typography.body2
    Row(Modifier.padding(vertical = 4.dp)) {
        Text(header, color = Palette.greyDark, style = style, modifier = Modifier.weight(1f))
        LinkifyText(value, style = style, modifier = Modifier.weight(2f))
    }
}

class HistogramData(
    val r: IntArray,
    val g: IntArray,
    val b: IntArray,
)

private fun process(bitmap: Bitmap): HistogramData {
    Timber.d("process...")
    val r = IntArray(256)
    val g = IntArray(256)
    val b = IntArray(256)
    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

    for (p in pixels) {
        r[android.graphics.Color.red(p)]++
        g[android.graphics.Color.green(p)]++
        b[android.graphics.Color.blue(p)]++
    }

    return HistogramData(r, g, b)

}

@Preview
@Composable
fun InfoPreview() {
    PhotosightTheme {
        ModalBottomSheetLayout(
            {
                InfoSheet(
                    Photo(
                        2,
                        "https://cdny.de/p/t/4/93a/7256654.jpg",
                        "",
                        "https://sight.photo/photos/7256852",
                        "The Title",
                        "John Smith",
                        "google.com",
                        "key"
                    ),
                    true
                )
            },
            sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded),
            sheetBackgroundColor = MaterialTheme.colors.primary,
        ) {}
    }
}