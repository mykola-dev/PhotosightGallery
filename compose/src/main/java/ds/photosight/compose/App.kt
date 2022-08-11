package ds.photosight.compose

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import ds.photosight.http_client.httpClient
import timber.log.Timber

@HiltAndroidApp
class App : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader = ImageLoader
        .Builder(this)
        .okHttpClient { httpClient.newBuilder().build() }
        .build()
}