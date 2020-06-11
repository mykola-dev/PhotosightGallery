package ds.photosight

import android.content.res.Configuration
import androidx.multidex.MultiDexApplication
import ds.photosight.di.appModule
import ds.photosight.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@App)
            modules(appModule, viewModelsModule)
            androidLogger()
        }
    }

    fun isPortrait(): Boolean {
        return resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE
    }


}


