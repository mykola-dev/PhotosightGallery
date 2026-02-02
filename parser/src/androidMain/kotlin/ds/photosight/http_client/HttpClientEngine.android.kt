package ds.photosight.http_client

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android

actual val httpClientEngine: HttpClientEngineFactory<*> = Android
