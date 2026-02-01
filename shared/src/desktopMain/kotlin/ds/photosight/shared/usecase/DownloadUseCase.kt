package ds.photosight.shared.usecase

import ds.photosight.shared.ui.model.Photo
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.nio.file.Files
import java.nio.file.Paths

actual class DownloadUseCase {
    actual suspend operator fun invoke(photo: Photo) {
        // Get Downloads folder path
        val downloadsDir = Paths.get(System.getProperty("user.home"), "Downloads")
        Files.createDirectories(downloadsDir)
        
        // Create target file path with photo title
        val fileName = "${photo.title}.jpg"
        val targetPath = downloadsDir.resolve(fileName)
        
        // Download image using Ktor
        val client = HttpClient()
        try {
            val response: ByteArray = client.get(photo.large).body()
            Files.write(targetPath, response)
        } finally {
            client.close()
        }
    }
}
