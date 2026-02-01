package ds.photosight.shared.ui.image

import androidx.compose.runtime.Composable

@Composable
actual fun buildPhotoImageRequest(
    url: String,
    cacheKey: String?,
    crossfade: Boolean
): Any {
    // Desktop Coil3 handles URLs directly without needing ImageRequest
    return url
}
