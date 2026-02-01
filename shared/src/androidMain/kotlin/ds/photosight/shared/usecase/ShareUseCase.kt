package ds.photosight.shared.usecase

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

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
        val file = context.loadImageFile(imageUrl)
        val uri = FileProvider.getUriForFile(context.applicationContext, context.packageName, file)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/jpeg"
            putExtra(Intent.EXTRA_TEXT, "Sent with Photosight Gallery")
            putExtra(Intent.EXTRA_STREAM, uri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        val shareIntent = Intent.createChooser(sendIntent, null).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        context.startActivity(shareIntent)
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
