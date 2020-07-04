package ds.photosight.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import ds.photosight.core.Prefs
import ds.photosight.repo.PhotosightRepo
import timber.log.Timber

class ViewerViewModel @ViewModelInject constructor(
    override val prefs: Prefs,
    override val log: Timber.Tree,
    private val photosightRepo: PhotosightRepo
) : BaseViewModel() {

    fun loadComments() {
        // todo
    }

}