package ds.photosight.shared.di

import ds.photosight.shared.repo.PhotosPagingSourceFactory
import ds.photosight.shared.repo.PhotosightRepo
import ds.photosight.shared.ui.screen.MainViewModel
import ds.photosight.shared.ui.screen.gallery.GalleryViewModel
import ds.photosight.shared.ui.screen.viewer.ViewerViewModel
import ds.photosight.shared.usecase.AppNameUseCase
import ds.photosight.shared.usecase.CheckVersionUseCase
import ds.photosight.shared.usecase.DownloadUseCase
import ds.photosight.shared.usecase.OpenBrowserUseCase
import ds.photosight.shared.usecase.ShareUseCase
import ds.photosight.shared.usecase.ToolbarDataUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun sharedModule(): Module = module {
    // Include platform-specific module
    includes(platformModule())

    // Repository
    singleOf(::PhotosightRepo)

    // Paging Source Factory
    factory<PhotosPagingSourceFactory> {
        object : PhotosPagingSourceFactory {
            override fun invoke(menuState: ds.photosight.shared.ui.screen.gallery.MenuState): ds.photosight.shared.repo.PhotosPagingSource {
                return ds.photosight.shared.repo.PhotosPagingSource(menuState, get())
            }
        }
    }

    // Use Cases
    singleOf(::AppNameUseCase)
    singleOf(::ToolbarDataUseCase)
    singleOf(::CheckVersionUseCase)
    singleOf(::ShareUseCase)
    singleOf(::DownloadUseCase)
    singleOf(::OpenBrowserUseCase)

    // ViewModels
    factory { MainViewModel(get()) }
    factory { GalleryViewModel(get(), get(), get()) }
    factory { ViewerViewModel(get(), get(), get(), get()) }
}
