package ds.photosight

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Configuration
import android.os.Environment
import android.preference.PreferenceManager
import android.view.View
import android.view.ViewConfiguration
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.androidquery.callback.BitmapAjaxCallback
import com.androidquery.util.AQUtility
import ds.photosight.utils.L
import java.io.File
import kotlin.platform.platformName
import kotlin.platform.platformStatic
import kotlin.properties.Delegates

public class App : Application(), Constants {


    override fun onCreate() {
        super<Application>.onCreate()
        _instance = this
        isLowRes = _prefs.getBoolean("lowres", false)

        forceOverflowMenu(true)

        aqueryConfig()
    }


    private fun aqueryConfig() {
        //set the max number of images (image width > 50) to be cached in memory, default is 20
        BitmapAjaxCallback.setCacheLimit(100)
        BitmapAjaxCallback.setIconCacheLimit(50)

        //set the max size of an image to be cached in memory, default is 1600 pixels (ie. 400x400)
        BitmapAjaxCallback.setPixelLimit(500 * 500)


        //set the max size of the memory cache, default is 1M pixels (4MB)
        BitmapAjaxCallback.setMaxPixelLimit(getTotalMemorySize().toInt())

        setCacheDir()
    }


    public fun setCacheDir() {
        val dir: File
        if (!_prefs.getBoolean(Constants.PREFS_KEY_USE_INTERNAL_CACHE_DIR, true)) {
            val extPath = _prefs.getString(Constants.PREFS_KEY_CACHE_PATH, getString(R.string.default_cache_dir))
            dir = File(Environment.getExternalStorageDirectory(), extPath)
        } else {
            dir = File(getCacheDir(), "aquery")
        }

        L.v("setting cache dir " + dir)
        AQUtility.setCacheDir(dir)
    }


    public fun getTotalMemorySize(): Long {

        val mi = ActivityManager.MemoryInfo()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        val availableMegs = mi.availMem / 2
        L.v("total memory=" + availableMegs)
        return availableMegs

    }


    override fun onLowMemory() {
        BitmapAjaxCallback.clearCache()
        L.e("LOW MEMORY!")
    }


    public fun forceOverflowMenu(enabled: Boolean) {
        try {
            val config = ViewConfiguration.get(this)
            val menuKeyField = javaClass<ViewConfiguration>().getDeclaredField("sHasPermanentMenuKey")
            menuKeyField.setAccessible(true)
            menuKeyField.setBoolean(config, !enabled)
        } catch (ex: Exception) {
            // Ignore
        }

    }


    public fun isPortrait(): Boolean {
        return getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE
    }


    companion object {
        public val _prefs: SharedPreferences by Delegates.lazy { PreferenceManager.getDefaultSharedPreferences(App._instance) }
        public var isLowRes: Boolean = false
        var _instance: App? = null



        platformStatic fun getInstance():App? = _instance
        platformStatic fun getPrefs():SharedPreferences? = _prefs

    }


}


