package ds.photosight.http_client

import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Inet4Address
import java.net.InetAddress

class DnsV4() : Dns {
    override fun lookup(hostname: String): List<InetAddress> = Dns.SYSTEM.lookup(hostname).filterIsInstance<Inet4Address>()
}

val httpClient = OkHttpClient.Builder().dns(DnsV4()).build()

fun runHttpRequest(url: String, map: Map<String, String>): String {
    val request: Request = Request.Builder()
        .addHeader("Cookie", map.toList().joinToString("; ", transform = { "${it.first}=${it.second}" }))
        .url(url)
        .build()
    return httpClient.newCall(request).execute().use { response -> response.body!!.string() }
}