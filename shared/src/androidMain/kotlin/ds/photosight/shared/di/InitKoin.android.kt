package ds.photosight.shared.di

import android.content.Context
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

/**
 * Android-specific Koin initialization with Context.
 */
fun initKoin(context: Context): KoinApplication {
    // Initialize Napier for multiplatform logging
    Napier.base(DebugAntilog())
    
    return startKoin {
        androidLogger()
        androidContext(context)
        modules(sharedModule())
    }
}
