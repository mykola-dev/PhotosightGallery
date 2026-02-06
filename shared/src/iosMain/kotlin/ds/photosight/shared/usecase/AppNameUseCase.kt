package ds.photosight.shared.usecase

import platform.Foundation.NSBundle

actual class AppNameUseCase {
    actual operator fun invoke(): String {
        return NSBundle.mainBundle.infoDictionary?.get("CFBundleDisplayName") as? String
            ?: NSBundle.mainBundle.infoDictionary?.get("CFBundleName") as? String
            ?: "Photosight"
    }
}
