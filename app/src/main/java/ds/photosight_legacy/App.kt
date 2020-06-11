package ds.photosight_legacy

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Environment
import android.preference.PreferenceManager
import com.androidquery.callback.BitmapAjaxCallback
import com.androidquery.util.AQUtility
import ds.photosight.R
import ds.photosight.utils.L
import timber.log.Timber
import java.io.File

class App : Application(), Constants {


    override fun onCreate() {
        super.onCreate()
        _instance = this
        isLowRes = prefs.getBoolean("lowres", false)

        aqueryConfig()

        Timber.plant(Timber.DebugTree())
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


    fun setCacheDir() {
        val dir: File= if (!prefs.getBoolean(Constants.PREFS_KEY_USE_INTERNAL_CACHE_DIR, true)) {
            val extPath = prefs.getString(Constants.PREFS_KEY_CACHE_PATH, getString(R.string.default_cache_dir))
            File(Environment.getExternalStorageDirectory(), extPath)
        } else {
            File(cacheDir, "aquery")
        }

        L.v("setting cache dir $dir")
        AQUtility.setCacheDir(dir)
    }


    private fun getTotalMemorySize(): Long {

        val mi = ActivityManager.MemoryInfo()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        val availableMegs = mi.availMem / 2
        L.v("total memory=$availableMegs")
        return availableMegs

    }


    override fun onLowMemory() {
        super.onLowMemory()
        BitmapAjaxCallback.clearCache()
        L.e("LOW MEMORY!")
    }


    fun isPortrait(): Boolean {
        return resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE
    }


    companion object {
        val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(_instance) }
        var isLowRes: Boolean = false
        lateinit var _instance: App

        fun getInstance(): App = _instance

    }


}


