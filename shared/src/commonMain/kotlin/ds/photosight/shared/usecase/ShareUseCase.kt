package ds.photosight.shared.usecase

expect class ShareUseCase() {
    fun shareUrl(pageUrl: String)
    suspend fun shareImage(imageUrl: String)
}
