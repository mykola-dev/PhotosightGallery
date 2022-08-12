package ds.photosight.compose.usecase

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import coil.imageLoader
import dagger.hilt.android.qualifiers.ApplicationContext
import ds.photosight.compose.R
import ds.photosight.compose.util.loadImageFile
import javax.inject.Inject


class ShareUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun shareUrl(pageUrl: String) {
        context.shareUrl(pageUrl)
    }

    fun shareImage(imageUrl: String) {
        context.shareImage(imageUrl)
    }

    private fun Context.shareUrl(pageUrl: String) {
        val share = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subj))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + "\n" + pageUrl)
        }
        startActivity(Intent.createChooser(share, getString(R.string.share_link)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun Context.shareImage(imageUrl: String) {
        val file = imageLoader.loadImageFile(imageUrl)
        val uri = FileProvider.getUriForFile(applicationContext, packageName, file)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/jpeg"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
            putExtra(Intent.EXTRA_STREAM, uri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        val shareIntent = Intent.createChooser(sendIntent, null).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        startActivity(shareIntent)
    }


}