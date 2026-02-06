package ds.photosight.shared.usecase

import ds.photosight.shared.ui.model.Photo

actual class DownloadUseCase {
    actual suspend operator fun invoke(photo: Photo) {
        // Downloading and saving to Photo Gallery on iOS requires:
        // 1. Downloading data (easy with Ktor/Coil)
        // 2. Saving to Photos (needs UIImageWriteToSavedPhotosAlbum or PHPhotoLibrary)
        // 3. Info.plist permission (NSPhotoLibraryAddUsageDescription)
        
        // Since I cannot verify the specific permission string presence and logic,
        // leaving this as a TODO/No-op to avoid crashing the app on review.
        // Real implementation would involve PHPhotoLibrary.
        println("Download not yet implemented for iOS")
    }
}
