package ds.photosight.core

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import ds.photosight.R
import java.io.File


fun Context.shareUrl(pageUrl: String) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "text/plain"
    share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subj))
    share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + "\n" + pageUrl)
    startActivity(Intent.createChooser(share, getString(R.string.share_link)))
}

suspend fun Context.shareImage(imageUrl: String) {
    val file = loadGlideFile(imageUrl)
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

fun Context.openInBrowser(url: String) {
    val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(i)
}

suspend fun Context.savePhoto(imageUrl: String) {
    val file = loadGlideFile(imageUrl)
    // todo
}