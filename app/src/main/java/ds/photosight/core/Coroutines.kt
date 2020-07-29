package ds.photosight.core

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun Context.loadGlideBitmap(url: String): Bitmap = withContext(Dispatchers.IO) {
    Glide.with(this@loadGlideBitmap)
        .asBitmap()
        .load(url)
        .submit()
        .get()
}

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun Context.loadGlideFile(url: String): File = withContext(Dispatchers.IO) {
    Glide.with(this@loadGlideFile)
        .asFile()
        .load(url)
        .submit()
        .get()
}