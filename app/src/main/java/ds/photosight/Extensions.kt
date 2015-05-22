package ds.photosight

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView

public fun Activity.showAbout() {
    val ver: String
    try {
        val manager = getPackageManager().getPackageInfo(getPackageName(), 0)
        ver = manager.versionName
    } catch (e1: PackageManager.NameNotFoundException) {
        ver = "100500"
    }


    val dialog = Dialog(this, R.style.Theme_AppCompat_Dialog)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.about)
    val b = dialog.findViewById(R.id.aboutOK) as Button
    val text = dialog.findViewById(R.id.aboutText) as TextView
    val verText = dialog.findViewById(R.id.versionText) as TextView

    val s = getResources().getString(R.string.abouttext, getResources().getString(R.string.changelog),
            if (Constants.PRO_VERSION) Constants.MARKETS_URL_PRO[Constants.CURRENT_MARKET] else Constants.MARKETS_URL[Constants.CURRENT_MARKET])
    text.setText(s)
    verText.setText(ver)
    b.setOnClickListener(object : View.OnClickListener {

        override fun onClick(v: View) {
            dialog.cancel()
        }
    })
    dialog.getWindow().setWindowAnimations(android.R.style.Animation_Translucent)
    dialog.show()
}

public fun Context.getApp(): App = this.getApplicationContext() as App