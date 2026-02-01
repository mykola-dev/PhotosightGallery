package ds.photosight.compose

import android.app.Application
import ds.photosight.shared.di.initKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin and Napier from shared module
        // This ensures proper KMP architecture where initialization is in shared code
        initKoin(this)
    }
}
