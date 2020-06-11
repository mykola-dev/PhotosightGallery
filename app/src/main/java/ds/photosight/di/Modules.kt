package ds.photosight.di

import ds.photosight.core.Prefs
import ds.photosight.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber

val appModule = module {
    single<Timber.Tree> { Timber.DebugTree() }
    single { Prefs(androidContext()) }
    //single { ScenariosAccessibilityService.Companion::scriptRunner.liveData }
}

val viewModelsModule = module {
    viewModel { MainViewModel() }
}