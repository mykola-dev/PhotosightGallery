package ds.photosight.compose.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(val log: Timber.Tree) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext get() = viewModelScope.coroutineContext

    init {
        log.v("VM=${javaClass.simpleName} $this")
    }
}