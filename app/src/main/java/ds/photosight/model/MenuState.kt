package ds.photosight.model

data class MenuState(
    val categories: List<MenuItemState>,
    val ratings: List<MenuItemState>
) {
    companion object {
        const val MENU_CATEGORIES = 0
        const val MENU_RATINGS = 1
    }
}