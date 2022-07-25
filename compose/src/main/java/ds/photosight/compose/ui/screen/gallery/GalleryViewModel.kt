package ds.photosight.compose.ui.screen.gallery

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import ds.photosight.compose.ui.BaseViewModel
import ds.photosight.compose.util.AppNameProvider
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val appNameProvider: AppNameProvider,
    val log: Timber.Tree
) : BaseViewModel() {

    val appName: String get() = appNameProvider()

    val showAboutDialog = mutableStateOf(false)


}

