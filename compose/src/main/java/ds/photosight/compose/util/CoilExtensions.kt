package ds.photosight.compose.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import java.io.File

fun Context.loadImageFile(imageUrl: String): File = this
    .imageLoader
    .diskCache
    ?.get(imageUrl)
    ?.use { it.data.toFile() }
    ?: error("can't load image")

suspend fun Context.loadBitmap(imageUrl: String): Bitmap = (this
    .imageLoader
    .execute(
        ImageRequest
            .Builder(this)
            .allowHardware(false)
            .data(imageUrl)
            .build()
    )
    .takeIf { it is SuccessResult }
    ?.drawable as BitmapDrawable)
    .bitmap
