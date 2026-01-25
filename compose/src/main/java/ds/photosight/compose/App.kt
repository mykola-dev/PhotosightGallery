package ds.photosight.compose

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import ds.photosight.compose.di.appModule
import ds.photosight.http_client.httpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber
        Timber.plant(Timber.DebugTree())

        // Initialize Koin
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }

    override fun newImageLoader(): ImageLoader = ImageLoader
        .Builder(this)
        .okHttpClient { httpClient.newBuilder().build() }
        .build()
}