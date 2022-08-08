package ds.photosight.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import ds.photosight.compose.ui.model.MenuTabs
import ds.photosight.compose.util.log
import ds.photosight.compose.util.logCompositions
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
    /* val state by DebugVM.state.collectAsState(initial = null)

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
     }*/
    var restarter by remember { mutableStateOf(0) }
    LaunchedEffect(restarter) {
        while (true) {
            delay(1000)
            restarter++
        }
    }
    val counter = produceState(0) {
        log.v("producer restarted")
        while (true) {
            delay(2000)
            value++
            log.d("produced $value")
        }
    }

    val derived = derivedStateOf {
        log.d("inside derived")
        Data("counter ${counter.value}")
    }
    logCompositions("root.  counter=${counter.hashCode()} derived=${derived.hashCode()}")

    val new = restarter
    log.v("restarter $new")
    isolate {
        logCompositions(msg = "isolate")
        Widget(derived.value)
    }
}

class Data(
    var string: String
)

//@Stable
data class Data2(
    val string: String
)

@Composable
fun Widget(data: Data) {
    logCompositions("widget")

    Text(text = data.string, color = Color.White)
}
