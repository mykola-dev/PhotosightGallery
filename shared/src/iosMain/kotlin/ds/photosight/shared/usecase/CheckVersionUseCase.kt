package ds.photosight.shared.usecase

import ds.photosight.shared.core.Prefs
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.Foundation.NSBundle

actual class CheckVersionUseCase : KoinComponent {
    private val prefs: Prefs by inject()

    actual fun shouldShowAboutDialog(): Boolean {
        val currentVersionString = NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion") as? String ?: "0"
        // Try to parse as Int, fallback to 0 if complex string (though CFBundleVersion usually is build number)
        val currentVersion = currentVersionString.toIntOrNull() ?: 0
        
        return if (currentVersion != prefs.appVersion) {
            prefs.appVersion = currentVersion
            true
        } else {
            false
        }
    }
}
