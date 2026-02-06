package ds.photosight.shared

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import ds.photosight.shared.di.initKoin
import ds.photosight.shared.ui.navigation.SharedApp
import ds.photosight.shared.ui.screen.viewer.BackPressDispatcher
import ds.photosight.shared.ui.screen.viewer.LocalBackPressDispatcher
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    SharedApp()
}

fun initKoinIOS() {
    initKoin()
}
