package ds.photosight.shared.usecase

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class OpenBrowserUseCase : KoinComponent {
    private val context: Context by inject()

    actual operator fun invoke(pageUrl: String) {
        val i = Intent(Intent.ACTION_VIEW, pageUrl.toUri()).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        context.startActivity(i)
    }
}
