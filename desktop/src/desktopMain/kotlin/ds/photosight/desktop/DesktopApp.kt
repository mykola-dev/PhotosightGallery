package ds.photosight.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ds.photosight.shared.di.initKoin
import ds.photosight.shared.ui.navigation.SharedApp
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
    application {
        Window(
            title = stringResource(Res.string.app_name),
            onCloseRequest = ::exitApplication,
            icon = painterResource(Res.drawable.photosight)
        ) {
            SharedApp()
        }
    }
}
