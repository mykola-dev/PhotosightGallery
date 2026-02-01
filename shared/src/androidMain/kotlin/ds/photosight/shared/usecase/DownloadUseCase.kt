package ds.photosight.shared.usecase

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import ds.photosight.shared.ui.model.Photo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

actual class DownloadUseCase : KoinComponent {
    private val context: Context by inject()

    actual suspend operator fun invoke(photo: Photo) {
        val file = context.loadImageFile(photo.large)
        
        // Save to Downloads folder using MediaStore
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "${photo.title}.jpg")
            put(MediaStore.Downloads.MIME_TYPE, "image/jpeg")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        
        val uri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            values
        ) ?: error("Failed to create MediaStore entry")
        
        file.inputStream().use { input ->
            context.contentResolver.openOutputStream(uri)?.use { output ->
                input.copyTo(output)
            } ?: error("can't open stream")
        }
    }

    private suspend fun Context.loadImageFile(imageUrl: String): File {
        val request = ImageRequest.Builder(this)
            .data(imageUrl)
            .build()
        val result = imageLoader.execute(request)
        if (result is SuccessResult) {
            val cacheKey = result.diskCacheKey ?: error("image wasn't saved to disk")
            val diskCache = imageLoader.diskCache ?: error("disk cache not available")
            val snapshot = diskCache.openSnapshot(cacheKey) ?: error("can't find image in cache")
            return snapshot.use { it.data.toFile() }
        } else {
            error("can't load image")
        }
    }
}
