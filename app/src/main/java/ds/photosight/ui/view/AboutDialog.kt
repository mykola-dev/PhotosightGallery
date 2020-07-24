package ds.photosight.ui.view

import android.app.Activity
import android.content.pm.PackageManager
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ds.photosight.R


internal fun Activity.showAbout() {
    val version: String = try {
        val manager = packageManager.getPackageInfo(packageName, 0)
        manager.versionName
    } catch (e1: PackageManager.NameNotFoundException) {
        "100500"
    }

    val message = getString(R.string.abouttext, getString(R.string.changelog))
    val spannable = SpannableString(message)
    Linkify.addLinks(spannable, Linkify.EMAIL_ADDRESSES)

    MaterialAlertDialogBuilder(this)
        .setTitle(getString(R.string.about_title_) + version)
        .setMessage(spannable)
        .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
        .create()
        .apply {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            show()

            // clickable links
            findViewById<TextView>(android.R.id.message)!!.movementMethod = LinkMovementMethod.getInstance()
        }

}