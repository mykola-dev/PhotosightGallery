package ds.photosight.http_client

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.java.Java

actual val httpClientEngine: HttpClientEngineFactory<*> = Java
