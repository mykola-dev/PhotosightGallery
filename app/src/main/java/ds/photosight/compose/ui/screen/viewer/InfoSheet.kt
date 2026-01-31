package ds.photosight.compose.ui.screen.viewer

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ds.photosight.compose.R
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.ui.theme.PhotosightTheme
import ds.photosight.compose.core.widget.Histogram
import ds.photosight.compose.core.widget.HistogramData
import ds.photosight.compose.core.widget.LinkifyText
import ds.photosight.compose.util.loadBitmap

@Composable
fun InfoSheet(photo: Photo, visible: Boolean) {
    Column(
            Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                    .navigationBarsPadding()
    ) {
        Text(stringResource(R.string.photo_details), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
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
fun InfoRow(header: String, value: String) {
    val style = MaterialTheme.typography.bodyMedium
    Row(Modifier.padding(vertical = 4.dp)) {
        Text(header, color = Palette.greyDark, style = style, modifier = Modifier.weight(1f))
        LinkifyText(value, style = style, modifier = Modifier.weight(2f))
    }
}

private fun process(bitmap: Bitmap): HistogramData {
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
        // ModalBottomSheetLayout removed due to migration
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
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
        }
    }
}
