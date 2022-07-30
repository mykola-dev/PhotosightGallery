package ds.photosight.compose.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.core.content.FileProvider
import ds.photosight.compose.R
import java.io.File


fun Context.shareUrl(pageUrl: String) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "text/plain"
    share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subj))
    share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + "\n" + pageUrl)
    startActivity(Intent.createChooser(share, getString(R.string.share_link)))
}

suspend fun Context.shareImage(imageUrl: String) {
    val file = loadImageFile(imageUrl)
    val uri = FileProvider.getUriForFile(applicationContext, packageName, file)
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "image/jpeg"
        putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
        putExtra(Intent.EXTRA_STREAM, uri)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}

fun loadImageFile(imageUrl: String): File {
    TODO()
}

fun Context.openInBrowser(url: String) {
    val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(i)
}

class SaveImage : ActivityResultContract<String, Uri?>() {
    @CallSuper
    override fun createIntent(context: Context, input: String): Intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        .setType("image/jpeg")
        .putExtra(Intent.EXTRA_TITLE, input)
        .addCategory(Intent.CATEGORY_OPENABLE)

    override fun getSynchronousResult(context: Context, input: String): SynchronousResult<Uri?>? = null

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? =
        if (intent == null || resultCode != Activity.RESULT_OK) null
        else intent.data
}