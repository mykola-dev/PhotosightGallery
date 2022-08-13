package ds.photosight.compose.ui.model

data class PhotoStats(
    val views: Int = 0,
    val art: Int = 0,
    val original: Int = 0,
    val tech: Int = 0,
    val likes: Int = 0,
    val dislikes: Int = 0
) {
    val rating: Float = 1 / (likes + dislikes).toFloat() * likes.toFloat()
}