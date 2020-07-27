package ds.photosight.core

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
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

fun Context.shareImage(imageUrl: String) {
    Glide.with(this)
        .asFile()
        .load(imageUrl)
        .into(object : CustomTarget<File>() {
            override fun onResourceReady(file: File, transition: Transition<in File>?) {
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

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })

}