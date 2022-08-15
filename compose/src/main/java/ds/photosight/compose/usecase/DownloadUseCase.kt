package ds.photosight.compose.usecase

import android.content.Context
import android.net.Uri
import coil.imageLoader
import dagger.hilt.android.qualifiers.ApplicationContext
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.util.loadImageFile
import javax.inject.Inject

class DownloadUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    operator fun invoke(photo: Photo, uri: Uri) {
        val file = context.imageLoader.loadImageFile(photo.large)
        context
            .contentResolver
            .openOutputStream(uri)
            ?.use {
                it.write(file.inputStream().readBytes())
            }
            ?: error("can't open stream")
    }
}