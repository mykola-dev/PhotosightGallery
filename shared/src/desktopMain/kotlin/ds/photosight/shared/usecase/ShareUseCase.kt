package ds.photosight.shared.usecase

import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI

actual class ShareUseCase {
    actual fun shareUrl(pageUrl: String) {
        // Copy to clipboard on desktop
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(StringSelection(pageUrl), null)
    }

    actual suspend fun shareImage(imageUrl: String) {
        // Open in browser on desktop
        Desktop.getDesktop().browse(URI(imageUrl))
    }
}
