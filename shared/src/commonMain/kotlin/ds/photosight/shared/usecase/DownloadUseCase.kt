package ds.photosight.shared.usecase

import ds.photosight.shared.ui.model.Photo

expect class DownloadUseCase() {
    suspend operator fun invoke(photo: Photo)
}
