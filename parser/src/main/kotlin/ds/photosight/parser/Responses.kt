package ds.photosight.parser


data class PhotoCategory(
    val index: Int,
    val name: String
)

data class PhotoInfo(
    val id: Int,
    val thumb: String,
    val large: String,
    val pageUrl: String,
    val title: String,
    val authorName: String,
    val authorUrl: String?,
    var paginationKey: String?,
    var failed: Boolean = false
)

data class PhotoDetails(
    val comments: List<Comment>,
    val awards: List<Award>,
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
        val likes: Int,
        val dislikes: Int,
        val views: Int
    )

    enum class Award {
        is_week_top,
        is_month_top,
        is_top,
        is_top_art,
        is_top_orig,
        is_top_tech,
        is_month_top_20,
        is_month_top_200,
        is_week_top_20,
        is_week_top_50,
        is_editors_choice,
        is_editors_choice_photoday;

        companion object {
            fun fromString(string: String): Award? = try {
                valueOf(string)
            } catch (e: IllegalArgumentException) {
                null
            }
        }

    }
}