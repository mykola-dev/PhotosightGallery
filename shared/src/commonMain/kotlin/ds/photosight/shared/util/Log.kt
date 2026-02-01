package ds.photosight.shared.util

import io.github.aakira.napier.Napier

object log {
    fun v(message: String) = Napier.v(message)
    fun d(message: String) = Napier.d(message)
    fun i(message: String) = Napier.i(message)
    fun w(message: String) = Napier.w(message)
    fun e(message: String, throwable: Throwable? = null) = 
        if (throwable != null) Napier.e(message, throwable) else Napier.e(message)
}
