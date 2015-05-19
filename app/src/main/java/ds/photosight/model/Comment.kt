package ds.photosight.model

import java.io.Serializable
import java.util.Date

public data class Comment(
        var content: String? = null,
        var name: String? = null,
        var status: String? = null,
        var avatarUrl: String? = null,
        var rate: String? = null,
        var dateRaw: String? = null,
        var dateFormatted: String? = null,
        var date: Date? = null
) : Serializable {


}
