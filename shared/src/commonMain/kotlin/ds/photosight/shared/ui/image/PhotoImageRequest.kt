package ds.photosight.shared.ui.image

import androidx.compose.runtime.Composable

/**
 * Platform-specific image request builder for Coil3.
 * 
 * Android: Uses context-based ImageRequest with cache keys for smooth transitions
 * Desktop: Uses simple URL model (Coil3 handles it differently on Desktop)
 * 
 * @param url The image URL to load
 * @param cacheKey Optional cache key for placeholder (enables smooth shared element transitions)
 * @param crossfade Whether to enable crossfade animation
 * @return Platform-specific model (ImageRequest on Android, String on Desktop)
 */
@Composable
expect fun buildPhotoImageRequest(
    url: String,
    cacheKey: String? = null,
    crossfade: Boolean = false
): Any
