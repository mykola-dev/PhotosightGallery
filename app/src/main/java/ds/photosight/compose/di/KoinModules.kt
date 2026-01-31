package ds.photosight.compose.di

import android.content.Context
import android.content.res.Resources
import ds.photosight.compose.repo.PhotosightRepo
import ds.photosight.compose.repo.PhotosPagingSource
import ds.photosight.compose.repo.PhotosPagingSourceFactory
import ds.photosight.compose.ui.screen.MainViewModel
import ds.photosight.compose.ui.screen.gallery.GalleryViewModel
import ds.photosight.compose.ui.screen.viewer.ViewerViewModel
import ds.photosight.compose.usecase.*
import ds.photosight.core.Prefs
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber

val appModule = module {

    // NOTE: androidContext() is automatically provided by Koin via startKoin in App.kt
    // We don't need to declare it as single { androidContext() } - that causes circular dependency

    // Core dependencies
    single { androidContext().resources }
    single<Timber.Tree> {
        println("timber init")
        Timber.DebugTree()
    }

    // Prefs
    single { Prefs(get()) }

    // Use Cases
    single { AppNameUseCase(get()) }
    single { PhotosightRepo(get()) }
    single { CheckVersionUseCase(get()) }
    single { ToolbarDataUseCase(get(), get()) }
    single { ShareUseCase(get()) }
    single { DownloadUseCase(get()) }
    single { OpenBrowserUseCase(get()) }

    // PhotosPagingSourceFactory - replace Hilt AssistedFactory pattern
    factory<PhotosPagingSourceFactory> {
        object : PhotosPagingSourceFactory {
            override fun invoke(menuState: ds.photosight.compose.ui.screen.gallery.MenuState): PhotosPagingSource {
                return PhotosPagingSource(menuState, get())
            }
        }
    }

    // ViewModels
    viewModel { MainViewModel(get(), get()) }
    viewModel { GalleryViewModel(get(), get(), get(), get()) }
    viewModel { ViewerViewModel(get(), get(), get(), get(), get()) }
}
