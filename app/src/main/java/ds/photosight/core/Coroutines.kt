package ds.photosight.core

import ds.photosight.parser.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> apiRequest(request: Request<T>): T = withContext(Dispatchers.Default) {
    request.invoke()
} 