package ds.photosight.model

data class MenuItemState(
    val menu: Int,
    val id: Int,
    val title: String,
    var isSelected: Boolean
)