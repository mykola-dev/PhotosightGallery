package ds.photosight.viewmodel

import L
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import ds.photosight.core.Prefs
import kotlinx.coroutines.CoroutineScope
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope, KoinComponent {
    override val coroutineContext: CoroutineContext = viewModelScope.coroutineContext

    protected val prefs: Prefs by inject()

    val showSnackbarCommand = LiveEvent<String>()

    init {
        L.v("${this::class.simpleName} init")
    }

    override fun onCleared() {
        super.onCleared()
        L.v("${this::class.simpleName} onCleared")
    }

}