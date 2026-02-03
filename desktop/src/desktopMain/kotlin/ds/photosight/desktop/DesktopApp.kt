package ds.photosight.desktop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ds.photosight.shared.di.initKoin
import ds.photosight.shared.ui.navigation.SharedApp
import ds.photosight.shared.ui.screen.viewer.BackPressDispatcher
import ds.photosight.shared.ui.screen.viewer.LocalBackPressDispatcher
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import photosight.shared.generated.resources.Res
import photosight.shared.generated.resources.app_name
import photosight.shared.generated.resources.photosight
import java.awt.SplashScreen

fun main() {
    try {
        SplashScreen.getSplashScreen()?.close()
    } catch (_: Exception) {}

    initKoin()
    val backPressDispatcher = BackPressDispatcher()
    application {
        Window(
            title = stringResource(Res.string.app_name),
            onCloseRequest = ::exitApplication,
            icon = painterResource(Res.drawable.photosight),
            onPreviewKeyEvent = {
                if (it.key == Key.Escape && it.type == KeyEventType.KeyUp) {
                    backPressDispatcher.onPress()
                } else false
            }
        ) {
            CompositionLocalProvider(LocalBackPressDispatcher provides backPressDispatcher) {
                Box(
                    Modifier.fillMaxSize()
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (event.type == PointerEventType.Press &&
                                        event.buttons.isSecondaryPressed
                                    ) {
                                        backPressDispatcher.onPress()
                                    }
                                }
                            }
                        }
                ) {
                    SharedApp()
                }
            }
        }
    }
}
