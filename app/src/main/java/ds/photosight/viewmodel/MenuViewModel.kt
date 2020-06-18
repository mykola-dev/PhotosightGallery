package ds.photosight.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import ds.photosight.core.Prefs

class MenuViewModel @ViewModelInject constructor(
    override val prefs: Prefs
) : BaseViewModel() {

    init {
        prefs.sampleData
    }
}