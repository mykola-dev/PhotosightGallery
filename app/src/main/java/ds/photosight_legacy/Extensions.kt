package ds.photosight_legacy

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.view.Window
import android.widget.Button
import android.widget.TextView
import ds.photosight.R

fun Activity.showAbout() {
    val ver: String = try {
        val manager = packageManager.getPackageInfo(packageName, 0)
        manager.versionName
    } catch (e1: PackageManager.NameNotFoundException) {
        "100500"
    }


    val dialog = Dialog(this, R.style.Theme_AppCompat_Dialog)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.about)
    val b = dialog.findViewById(R.id.aboutOK) as Button
    val text = dialog.findViewById(R.id.aboutText) as TextView
    val verText = dialog.findViewById(R.id.versionText) as TextView

    val s = getResources().getString(R.string.abouttext, getResources().getString(R.string.changelog),
            if (Constants.PRO_VERSION) Constants.MARKETS_URL_PRO[Constants.CURRENT_MARKET] else Constants.MARKETS_URL[Constants.CURRENT_MARKET])
    text.text = s
    verText.text = ver
    b.setOnClickListener { dialog.cancel() }
    dialog.window.setWindowAnimations(android.R.style.Animation_Translucent)
    dialog.show()
}

fun Context.getApp(): App = this.applicationContext as App