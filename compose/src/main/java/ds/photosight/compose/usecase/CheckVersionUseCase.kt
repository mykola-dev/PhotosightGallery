package ds.photosight.compose.usecase

import ds.photosight.compose.BuildConfig
import ds.photosight.core.Prefs
import javax.inject.Inject


class CheckVersionUseCase @Inject constructor(
    private val prefs: Prefs,
) {

    fun shouldShowAboutDialog(): Boolean {
        val appVersion = BuildConfig.VERSION_CODE
        return if (appVersion != prefs.appVersion) {
            prefs.appVersion = appVersion
            true
        } else {
            false
        }
    }
}