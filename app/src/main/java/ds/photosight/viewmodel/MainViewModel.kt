package ds.photosight.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import ds.photosight.core.Prefs

class MainViewModel @ViewModelInject constructor(
    override val prefs: Prefs
) : BaseViewModel() {

    init {
        prefs.sampleData
    }
}