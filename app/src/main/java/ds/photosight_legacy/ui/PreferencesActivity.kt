package ds.photosight_legacy.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.preference.PreferenceScreen
import android.widget.Toast
import ds.photosight.R
import ds.photosight_legacy.App
import ds.photosight_legacy.Constants
import ds.photosight_legacy.showAbout
import ds.photosight.utils.L
import ds.photosight_legacy.utils.Utils

class PreferencesActivity : PreferenceActivity(), Constants, SharedPreferences.OnSharedPreferenceChangeListener {

    private val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.prefs)
        prefs.registerOnSharedPreferenceChangeListener(this)

    }


    override fun onDestroy() {
        prefs.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }


    override fun onSharedPreferenceChanged(p: SharedPreferences, key: String) {
        if (key == Constants.PREFS_KEY_CACHE_PATH || key == (Constants.PREFS_KEY_USE_INTERNAL_CACHE_DIR)) {
            L.v("cache setting changed")
            App.getInstance().setCacheDir()
        }

    }


    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen?, preference: Preference): Boolean {
        when (preference.key) {
            //Constants.PREFS_KEY_SHAREAPP -> shareApp()
            Constants.PREFS_KEY_ABOUT -> showAbout()
            Constants.PREFS_KEY_DONATE -> donate()
            Constants.PREFS_KEY_CLEAR_CACHE -> showClearCacheDialog()
            Constants.PREFS_KEY_LOW_RES -> App.isLowRes = prefs.getBoolean(Constants.PREFS_KEY_LOW_RES, false)
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }


    private fun donate() {
        Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
    }


    private fun showClearCacheDialog() {
        showDialog(1)
    }



    override fun onCreateDialog(id: Int): Dialog {
        val d = AlertDialog.Builder(this)
        when (id) {
            1 -> {
                d.setTitle(R.string.clear_cache)
                d.setMessage(R.string.do_you_want_to_clear_the_cache_now_)
                d.setIcon(android.R.drawable.ic_dialog_alert)
                d.setPositiveButton(android.R.string.yes, object : OnClickListener {

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        Utils.clearCaches(applicationContext)

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
