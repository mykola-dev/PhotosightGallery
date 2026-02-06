package ds.photosight.shared.usecase

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class OpenBrowserUseCase {
    actual operator fun invoke(pageUrl: String) {
        val url = NSURL(string = pageUrl)
        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}
