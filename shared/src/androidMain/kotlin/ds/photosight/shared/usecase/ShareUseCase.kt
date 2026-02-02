package ds.photosight.shared.usecase

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.IOException

actual class ShareUseCase : KoinComponent {
    private val context: Context by inject()

    actual fun shareUrl(pageUrl: String) {
        val share = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check this out!")
            putExtra(Intent.EXTRA_TEXT, "Sent with Photosight Gallery\n$pageUrl")
        }
        context.startActivity(Intent.createChooser(share, "Share URL").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    actual suspend fun shareImage(imageUrl: String) {
        try {
            val file = withContext(Dispatchers.IO) {
                context.copyImageToCache(imageUrl)
            }
            
            // Ensure the file exists and is readable
            if (!file.exists()) {
                error("Shared file doesn't exist")
            }
            
            val uri = FileProvider.getUriForFile(
                context.applicationContext, 
                context.packageName, 
                file
            )
            
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "image/jpeg"
                putExtra(Intent.EXTRA_TEXT, "Sent with Photosight Gallery")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val shareIntent = Intent.createChooser(sendIntent, "Share Image").apply { 
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) 
            }
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            // Log error and rethrow to let UI handle it
            android.util.Log.e("ShareUseCase", "Failed to share image", e)
            throw e
        }
    }

    private suspend fun Context.copyImageToCache(imageUrl: String): File {
        // First, ensure the image is loaded and cached by Coil
        val request = ImageRequest.Builder(this)
            .data(imageUrl)
            .build()
        
        val result = imageLoader.execute(request)
        
        if (result is SuccessResult) {
            val cacheKey = result.diskCacheKey ?: error("Image wasn't saved to disk cache")
            val diskCache = imageLoader.diskCache ?: error("Disk cache not available")
            
            // Get the cached file from Coil's disk cache
            val cachedFile = diskCache.openSnapshot(cacheKey)?.use { snapshot ->
                snapshot.data.toFile()
            } ?: error("Can't find image in cache")
            
            // Create a temporary file in app's cache directory for sharing
            // This ensures it's in a location FileProvider can access
            val shareDir = File(cacheDir, "shared_images").apply {
                if (!exists()) mkdirs()
            }
            
            // Generate a unique filename
            val timestamp = System.currentTimeMillis()
            val outputFile = File(shareDir, "shared_image_$timestamp.jpg")
            
            // Copy the file to our cache directory
            try {
                cachedFile.inputStream().use { input ->
                    outputFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                
                // Make file readable
                outputFile.setReadable(true, false)
                
                return outputFile
            } catch (e: IOException) {
                outputFile.delete()
                throw error("Failed to copy image for sharing: ${e.message}")
            }
        } else {
            error("Can't load image - result was not success: $result")
        }
    }
}
