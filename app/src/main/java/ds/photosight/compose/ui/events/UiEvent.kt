package ds.photosight.compose.ui.events

import androidx.annotation.StringRes

interface UiEvent {
    class Toast(val message: String) : UiEvent
    class Retry : UiEvent
    class Snack(@param:StringRes val stringId: Int) : UiEvent
    class OpenInfo : UiEvent
}
