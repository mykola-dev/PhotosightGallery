package ds.photosight.viewmodel

import L
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import ds.photosight.core.Prefs
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel() : ViewModel(), CoroutineScope {
    abstract val prefs: Prefs
    abstract val log: Timber.Tree

    override val coroutineContext: CoroutineContext = viewModelScope.coroutineContext

    val showSnackbarCommand = LiveEvent<String>()

    init {
        L.v("${this::class.simpleName} init")

    }

    override fun onCleared() {
        super.onCleared()
        L.v("${this::class.simpleName} onCleared")
    }

}