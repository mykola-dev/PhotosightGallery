package ds.photosight.shared.ui.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
actual fun buildPhotoImageRequest(
    url: String,
    cacheKey: String?,
    crossfade: Boolean
): Any {
    val context = LocalContext.current
    return ImageRequest.Builder(context)
        .data(url)
        .apply {
            if (cacheKey != null) {
                placeholderMemoryCacheKey(cacheKey)
            }
            if (crossfade) {
                crossfade(true)
            }
        }
        .build()
}
