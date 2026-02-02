package ds.photosight.shared.core

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import com.russhwolf.settings.get

class Prefs(private val settings: Settings) {
    var appVersion: Int
        get() = settings.getInt(KEY_APP_VERSION, 0)
        set(value) { settings[KEY_APP_VERSION] = value }

    companion object {
        private const val KEY_APP_VERSION = "app_version"
    }
}
