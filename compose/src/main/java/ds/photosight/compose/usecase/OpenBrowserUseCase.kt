package ds.photosight.compose.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class OpenBrowserUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    operator fun invoke(pageUrl: String) {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(pageUrl)).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        context.startActivity(i)
    }

}