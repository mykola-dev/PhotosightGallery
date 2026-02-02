package ds.photosight.http_client

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.isSuccess

actual val httpClient: HttpClient = HttpClient(Android) {
    install(HttpCookies)
    install(HttpRedirect)  // IMPORTANT: Enable redirect following!

    defaultRequest {
        header(
            "User-Agent",
            "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/120.0.0.0 Mobile Safari/537.36"
        )
    }

    // Timeout configuration
    install(HttpTimeout) {
        requestTimeoutMillis = 30000
        connectTimeoutMillis = 10000
    }
}

actual suspend fun runHttpRequest(url: String, cookies: Map<String, String>): String {
    val response = httpClient.get(url) {
        // Add cookies as headers
        if (cookies.isNotEmpty()) {
            header(
                "Cookie",
                cookies.toList().joinToString("; ") { "${it.first}=${it.second}" }
            )
        }
    }

    if (!response.status.isSuccess()) {
        throw Exception("Unexpected code ${response.status}")
    }

    return response.bodyAsText()
}

actual suspend fun downloadBytes(url: String): ByteArray {
    val response = httpClient.get(url)
    return response.readBytes()
}
