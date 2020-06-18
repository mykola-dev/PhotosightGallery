package ds.photosight.parser


data class PhotoCategory(
    val index: Int,
    val name: String
)

data class PhotoInfo(
    val id: Int,
    val thumb: String,
    val large: String,
    val page: String,
    val title: String,
    val authorName: String,
    val authorUrl: String?
)

data class PhotoDetails(
    val comments: List<Comment>,
    val avards: List<String>,
    val stats: Stats
) {
    data class Comment(
        val text: String,
        val dateRaw: String,
        val timestamp: Long,
        val author: String,
        val avatar: String,
        val likes: Int,
        val isAuthor: Boolean
    )

    data class Stats(
        val art: Int,
        val original: Int,
        val tech: Int,
        val like: Int,
        val dislike: Int,
        val views: Int
    )
}