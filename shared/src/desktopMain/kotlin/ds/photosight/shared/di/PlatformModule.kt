package ds.photosight.shared.di

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import io.ktor.client.engine.java.*
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.prefs.Preferences

actual fun platformModule(): Module = module {
    // Desktop (JVM)-specific Ktor engine
    single { Java.create() }
    
    // Multiplatform Settings - Desktop implementation using Java Preferences
    single<Settings> { 
        PreferencesSettings(Preferences.userRoot().node("photosight"))
    }
}
