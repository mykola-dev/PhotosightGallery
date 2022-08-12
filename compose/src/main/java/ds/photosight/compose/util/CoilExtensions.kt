package ds.photosight.compose.util

import coil.ImageLoader
import java.io.File

fun ImageLoader.loadImageFile(imageUrl: String): File = this
    .diskCache
    ?.also { log.v("cache dir=${it.directory}") }
    ?.get(imageUrl)
    ?.use { it.data.toFile() }
    ?: error("can't load image")
