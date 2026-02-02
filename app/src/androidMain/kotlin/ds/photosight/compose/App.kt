package ds.photosight.compose

import android.app.Application
import ds.photosight.shared.di.initKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin(this)
    }
}
