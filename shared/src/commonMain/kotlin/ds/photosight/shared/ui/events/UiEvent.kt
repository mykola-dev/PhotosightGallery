package ds.photosight.shared.ui.events

interface UiEvent {
    class Toast(val message: String) : UiEvent
    class Retry : UiEvent
    class Snack(val stringId: String) : UiEvent
    class OpenInfo : UiEvent
}
