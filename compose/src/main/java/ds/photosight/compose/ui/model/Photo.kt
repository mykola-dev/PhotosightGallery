package ds.photosight.compose.ui.model

data class Photo(
    val index: Int,
    val id: Int,
    val thumb: String,
    val large: String,
    val pageUrl: String,
    val title: String,
    val authorName: String,
    val authorUrl: String?,
    val paginationKey: String?,
) {
    val cacheKey: String = id.toString()
}


