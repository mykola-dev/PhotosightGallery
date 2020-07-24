package ds.photosight.ui.viewmodel

import L
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import ds.photosight.core.Prefs
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel() : ViewModel(), CoroutineScope {
    abstract val prefs: Prefs
    abstract val log: Timber.Tree

    override val coroutineContext: CoroutineContext = viewModelScope.coroutineContext

    init {
        L.v("${this::class.simpleName} init")

    }

    override fun onCleared() {
        super.onCleared()
        L.v("${this::class.simpleName} onCleared")
    }

}