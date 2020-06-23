package ds.photosight.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import ds.photosight.core.Prefs
import timber.log.Timber

class GalleryViewModel @ViewModelInject constructor(
    override val prefs: Prefs,
    override val log: Timber.Tree
) : BaseViewModel() {

    init {
        prefs.sampleData
    }
}