package ds.photosight.shared.usecase

import java.awt.Desktop
import java.net.URI

actual class OpenBrowserUseCase {
    actual operator fun invoke(pageUrl: String) {
        Desktop.getDesktop().browse(URI(pageUrl))
    }
}
