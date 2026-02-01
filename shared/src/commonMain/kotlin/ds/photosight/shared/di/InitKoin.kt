package ds.photosight.shared.di

import org.koin.core.KoinApplication

/**
 * Initialize Koin dependency injection.
 * Platform-specific implementations handle platform context and logger setup.
 */
expect fun initKoin(): KoinApplication
