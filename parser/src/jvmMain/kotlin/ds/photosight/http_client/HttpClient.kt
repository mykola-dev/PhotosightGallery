package ds.photosight.http_client

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

actual val httpClient: HttpClient = HttpClient(Java) {
    install(HttpCookies)
    install(HttpRedirect)  // IMPORTANT: Enable redirect following!

    defaultRequest {
        header(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
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
