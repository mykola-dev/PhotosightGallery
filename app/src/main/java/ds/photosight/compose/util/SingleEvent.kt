package ds.photosight.compose.util

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class SingleEvent<T>(
    private val source: MutableSharedFlow<T> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
) : SharedFlow<T> by source {

    operator fun invoke(data: T) {
        source.tryEmit(data)
    }
}

operator fun SingleEvent<Unit>.invoke() {
    invoke(Unit)
}