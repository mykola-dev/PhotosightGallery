package ds.photosight.compose.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ds.photosight.compose.ui.events.UiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(val log: Timber.Tree) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext get() = viewModelScope.coroutineContext

    val events: Flow<UiEvent> = MutableSharedFlow(extraBufferCapacity = 1024)

    protected fun event(e: UiEvent) {
        (events as MutableSharedFlow<UiEvent>).tryEmit(e)
    }

    init {
        log.v("VM=${javaClass.simpleName} $this")
    }
}