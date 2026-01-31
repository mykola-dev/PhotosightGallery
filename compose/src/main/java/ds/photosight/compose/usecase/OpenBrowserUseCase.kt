package ds.photosight.compose.usecase

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri


class OpenBrowserUseCase(private val context: Context) {

    operator fun invoke(pageUrl: String) {
        val i = Intent(Intent.ACTION_VIEW, pageUrl.toUri()).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        context.startActivity(i)
    }

}