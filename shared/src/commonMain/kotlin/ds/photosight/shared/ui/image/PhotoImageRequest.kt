package ds.photosight.shared.ui.image

import androidx.compose.runtime.Composable
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.*

/**
 * Builds a Coil3 ImageRequest for loading photos with shared element transition support.
 * Works across all platforms (Android, Desktop, iOS).
 *
 * Uses placeholderMemoryCacheKey to enable smooth shared element transitions:
 * - When opening a full-size image, it uses the thumbnail as a placeholder
 * - Creates a seamless zoom animation from thumbnail to full image
 *
 * @param url The image URL to load
 * @param cacheKey Optional cache key for placeholder (typically thumbnail cache key)
 * @param crossfade Whether to enable crossfade animation
 * @return ImageRequest configured for shared element transitions
 */
@Composable
fun buildPhotoImageRequest(
    url: String,
    cacheKey: String? = null,
    crossfade: Boolean = false
): ImageRequest {
    val context = LocalPlatformContext.current
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
