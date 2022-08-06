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

    Column(Modifier.systemBarsPadding()) {

        val tabIndex = 0
        /*TabRow(selectedTabIndex = tabIndex) {
            MenuTabs.values().forEach {
                Tab(
                    selected = false,
                    onClick = { },
                    text = { Text(text = it.name) }
                )
            }
        }*/
        val pagerState = rememberPagerState()
        HorizontalPager(
            count = MenuTabs.values().size,
            state = pagerState
        ) {

            val color = if (it == 0) Color.Red
            else Color.Blue
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
            )
        }
    }
}

@Composable
fun Widget(text: String) {
    log.v("recomposed")

    Text(text = text)
}
