package ds.photosight.shared.util

import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.PlatformContext
import coil3.annotation.DelicateCoilApi
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.aakira.napier.Napier

/**
 * Helper to preload images into Coil memory cache before navigation.
 * This ensures smooth shared element transitions without visual glitches.
 *
 * This is a multiplatform implementation using Coil 3.
 */
object ImagePreloader {

    /**
     * Preload an image into memory cache.
     * Returns true if successful, false otherwise.
     */
    @OptIn(DelicateCoilApi::class)
    suspend fun preload(context: PlatformContext, imageUrl: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val imageLoader = SingletonImageLoader.get(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .build()

            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                Napier.v("Image preloaded successfully: $imageUrl")
                true
            } else {
                Napier.e("Failed to preload image: $imageUrl")
                false
            }
        } catch (e: Exception) {
            Napier.e("Exception while preloading image: $imageUrl", e)
            false
        }
    }
}
