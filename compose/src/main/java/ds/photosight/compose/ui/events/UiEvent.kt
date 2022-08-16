package ds.photosight.compose.ui.events

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ds.photosight.compose.util.logCompositions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

interface UiEvent {
    class Toast(val message: String) : UiEvent
    class Retry : UiEvent
    class Snack(@StringRes val stringId: Int) : UiEvent
    class OpenInfo : UiEvent
}

@Composable
fun CollectEvents(eventsFlow: Flow<UiEvent>, onEvent: (UiEvent) -> Unit) {
    logCompositions("CollectEvents")
    LaunchedEffect(Unit) {
        eventsFlow.collectLatest { event ->
            onEvent(event)
        }
    }

}
