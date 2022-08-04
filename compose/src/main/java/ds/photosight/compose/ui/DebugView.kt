package ds.photosight.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ds.photosight.compose.util.log
import ds.photosight.compose.util.rememberDerived
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

object DebugVM {
    val state = flow {
        generateSequence(0) { it + 1 }
            .forEach {
                delay(10)
                emit(it)
            }
    }
}

@Composable
fun DebugView() {
    val state by DebugVM.state.collectAsState(initial = null)

    val text by rememberDerived {
        (state ?: 0) / 100
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray), contentAlignment = Alignment.Center
    ) {
        log.v("text=$text")
        Widget(text = "$text")
    }

    SideEffect {
    }
}

@Composable
fun Widget(text: String) {
    log.v("recomposed")

    Text(text = text)
}
