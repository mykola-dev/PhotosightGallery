package ds.photosight

import android.content.res.Configuration
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp
import ds.photosight.parser.DailyPhotosRequest
import ds.photosight.parser.debugEnabled
import timber.log.Timber

@HiltAndroidApp
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        // legacy
        Timber.plant(Timber.DebugTree())

        //DailyPhotosRequest(DailyPhotosRequest.DatePage(1,1,1))

        //debugEnabled = true
    }

    fun isPortrait(): Boolean {
        return resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE
    }


}


