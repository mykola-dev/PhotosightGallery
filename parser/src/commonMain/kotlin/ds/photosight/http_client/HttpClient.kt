package ds.photosight.http_client

import io.ktor.client.HttpClient

// Common expect declarations - platform-specific implementations in androidMain/jvmMain
expect val httpClient: HttpClient

// Platform-independent function to make HTTP requests
expect suspend fun runHttpRequest(url: String, cookies: Map<String, String>): String
