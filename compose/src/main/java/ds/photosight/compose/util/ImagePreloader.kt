package ds.photosight.compose.util

import android.content.Context
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Helper to preload images into Coil memory cache before navigation.
 * This ensures smooth shared element transitions without visual glitches.
 */
object ImagePreloader {

    /**
     * Preload an image into memory cache.
     * Returns true if successful, false otherwise.
     */
    suspend fun preload(context: Context, imageUrl: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val imageLoader = ImageLoader.Builder(context).build()
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .build()

            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                Timber.v("Image preloaded successfully: $imageUrl")
                true
            } else {
                Timber.e("Failed to preload image: $imageUrl")
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception while preloading image: $imageUrl")
            false
        }
    }
}
