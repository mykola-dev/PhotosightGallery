package ds.photosight.view

import android.app.Activity
import android.content.pm.PackageManager
import android.view.ViewGroup
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

    val d = MaterialAlertDialogBuilder(this)
        .setTitle(getString(R.string.about_title_) + version)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.cancel()
        }
        .create()
    d.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    d.show()

}