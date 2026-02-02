package ds.photosight.shared.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger

fun initKoin(): KoinApplication {
    // Initialize Napier for multiplatform logging
    Napier.base(DebugAntilog())
    
    return startKoin {
        // Use PrintLogger for desktop
        logger(PrintLogger(Level.DEBUG))
        modules(sharedModule())
    }
}
