package ds.photosight.shared.di

import android.content.Context
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.Settings
import ds.photosight.shared.core.Prefs
import io.ktor.client.engine.android.Android
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalSettingsImplementation::class)
actual fun platformModule(): Module = module {
    single { Android.create() }
    single<Settings> { Settings() }

    single { Prefs(get()) }

    // Platform-specific dependencies for UseCases
    single { get<Context>().packageManager }
    single { get<Context>().packageName }
}
