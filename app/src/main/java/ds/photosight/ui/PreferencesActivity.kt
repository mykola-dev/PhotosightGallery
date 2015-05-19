package ds.photosight.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.preference.PreferenceScreen
import android.view.View
import android.widget.Toast
import ds.photosight.App
import ds.photosight.Constants
import ds.photosight.R
import ds.photosight.utils.L
import ds.photosight.utils.ShareProgress
import ds.photosight.utils.Utils
import kotlin.properties.Delegates

public class PreferencesActivity : PreferenceActivity(), Constants, SharedPreferences.OnSharedPreferenceChangeListener {

    val prefs: SharedPreferences by Delegates.lazy { PreferenceManager.getDefaultSharedPreferences(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super<PreferenceActivity>.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.prefs)
        prefs.registerOnSharedPreferenceChangeListener(this)

    }


    override fun onDestroy() {
        prefs.unregisterOnSharedPreferenceChangeListener(this)
        super<PreferenceActivity>.onDestroy()
    }


    override fun onSharedPreferenceChanged(p: SharedPreferences, key: String) {
        if (key == Constants.PREFS_KEY_CACHE_PATH || key == (Constants.PREFS_KEY_USE_INTERNAL_CACHE_DIR)) {
            L.v("cache setting changed")
            App.getInstance()!!.setCacheDir()
        }


    }


    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen?, preference: Preference): Boolean {
        when (preference.getKey()) {
            "about" -> App.showAbout(this)
            Constants.PREFS_KEY_SHAREAPP -> shareApp()
            Constants.PREFS_KEY_DONATE -> donate()
            Constants.PREFS_KEY_CLEAR_CACHE -> showClearCacheDialog()
            Constants.PREFS_KEY_LOW_RES -> App.isLowRes = prefs.getBoolean(Constants.PREFS_KEY_LOW_RES, false)
        }

        return super<PreferenceActivity>.onPreferenceTreeClick(preferenceScreen, preference)
    }


    private fun donate() {
        Toast.makeText(this, getString(R.string.coming_soon), 0).show()
    }


    private fun showClearCacheDialog() {
        showDialog(1)
    }


    private fun shareApp() {
        val share = Intent(Intent.ACTION_SEND)
        share.setType("text/plain")
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app_subj))
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text) + " " + Constants.MARKETS_URL[Constants.CURRENT_MARKET])
        ShareProgress(this).execute(share)
    }


    override fun onCreateDialog(id: Int): Dialog {
        val d = AlertDialog.Builder(this)
        when (id) {
            1 -> {
                d.setTitle(R.string.clear_cache)
                d.setMessage(ds.photosight.R.string.do_you_want_to_clear_the_cache_now_)
                d.setIcon(android.R.drawable.ic_dialog_alert)
                d.setPositiveButton(android.R.string.yes, object : OnClickListener {

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        Utils.clearCaches(getApplicationContext())

                    }
                })
                d.setNegativeButton(android.R.string.no, object : OnClickListener {

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        dialog.cancel()
                    }
                })
            }
        }

        val diag = d.create()
        return diag
    }

}
