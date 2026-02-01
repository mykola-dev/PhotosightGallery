package ds.photosight.shared.usecase

import android.content.pm.PackageManager
import ds.photosight.shared.core.Prefs
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class CheckVersionUseCase : KoinComponent {
    private val prefs: Prefs by inject()
    private val packageManager: PackageManager by inject()
    private val packageName: String by inject()

    actual fun shouldShowAboutDialog(): Boolean {
        val appVersion = try {
            packageManager.getPackageInfo(packageName, 0).longVersionCode
        } catch (e: Exception) {
            0L
        }
        return if (appVersion != prefs.appVersion.toLong()) {
            prefs.appVersion = appVersion.toInt()
            true
        } else {
            false
        }
    }
}
