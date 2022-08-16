package ds.photosight.compose.usecase

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.util.loadImageFile
import javax.inject.Inject

class DownloadUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    operator fun invoke(photo: Photo, uri: Uri) {
        context
            .loadImageFile(photo.large)
            .inputStream()
            .use { input ->
                context
                    .contentResolver
                    .openOutputStream(uri)
                    ?.use { output ->
                        input.copyTo(output)
                    }
                    ?: error("can't open stream")
            }
    }
}