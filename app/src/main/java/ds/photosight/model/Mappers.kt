package ds.photosight.model

import ds.photosight.parser.PhotoCategory

fun PhotoCategory.toMenuItemState(): MenuItemState = MenuItemState(MenuState.MENU_CATEGORIES, index, name, false)