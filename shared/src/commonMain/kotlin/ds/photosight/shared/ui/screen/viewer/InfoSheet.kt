package ds.photosight.shared.ui.screen.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ds.photosight.shared.ui.model.Photo
import ds.photosight.shared.ui.theme.Palette
import ds.photosight.shared.core.widget.Histogram
import ds.photosight.shared.core.widget.HistogramData
import ds.photosight.shared.core.widget.LinkifyText
import org.jetbrains.compose.resources.stringResource
import photosight.shared.generated.resources.Res
import photosight.shared.generated.resources.photo_details
import photosight.shared.generated.resources.title
import photosight.shared.generated.resources.author
import photosight.shared.generated.resources.source

@Composable
fun InfoSheet(photo: Photo, visible: Boolean) {
    Column(
            Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                    .navigationBarsPadding()
    ) {
        Text(stringResource(Res.string.photo_details), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        InfoRow(stringResource(Res.string.title), photo.title)
        InfoRow(stringResource(Res.string.author), "${photo.authorName} ${photo.authorUrl}")
        InfoRow(stringResource(Res.string.source), photo.pageUrl)
        Spacer(Modifier.height(16.dp))

        // Bitmap processing for histogram is Android-specific and commented out for now
        var data by remember(photo) { mutableStateOf<HistogramData?>(null) }
        
        // TODO: Implement multiplatform bitmap processing for histogram
        // LaunchedEffect(photo, visible) {
        //     if (visible) {
        //         val bitmap = loadBitmap(photo.thumb)
        //         data = processHistogram(bitmap)
        //     }
        // }
        
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

// TODO: Implement multiplatform histogram processing
// private fun processHistogram(bitmap: ImageBitmap): HistogramData {
//     val r = IntArray(256)
//     val g = IntArray(256)
//     val b = IntArray(256)
//     // Process pixels...
//     return HistogramData(r, g, b)
// }
