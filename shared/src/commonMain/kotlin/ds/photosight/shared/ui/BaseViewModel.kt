package ds.photosight.shared.ui

import androidx.lifecycle.ViewModel
import ds.photosight.shared.ui.events.UiEvent
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel : ViewModel() {
    
    // Multiplatform ViewModel scope using coroutines
    val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    
    val events: Flow<UiEvent> = MutableSharedFlow(extraBufferCapacity = 1024)
    
    protected fun event(e: UiEvent) {
        (events as MutableSharedFlow<UiEvent>).tryEmit(e)
    }

    protected fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(context, start, block)
    
    protected fun logVerbose(message: String) {
        Napier.v(message)
    }
    
    protected fun logError(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Napier.e(message, throwable)
        } else {
            Napier.e(message)
        }
    }
    
    // Call this when the ViewModel is being disposed
    override fun onCleared() {
        Napier.d("ViewModel cleared: ${this::class.simpleName}")
        viewModelScope.cancel()
    }
    
    init {
        Napier.v("VM created: ${this::class.simpleName}")
    }
}
