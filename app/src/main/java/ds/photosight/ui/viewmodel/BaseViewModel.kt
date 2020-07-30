package ds.photosight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ds.photosight.core.Prefs
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {
    abstract val prefs: Prefs
    abstract val log: Timber.Tree

    override val coroutineContext: CoroutineContext = viewModelScope.coroutineContext

    override fun onCleared() {
        super.onCleared()
        log.v("${this::class.simpleName} onCleared")
    }

}