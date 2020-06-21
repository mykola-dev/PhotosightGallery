package ds.photosight.model

import ds.photosight.parser.PhotoCategory
import ds.photosight.view.MenuItemState
import ds.photosight.view.MenuState

fun PhotoCategory.toMenuItemState(position: Int): MenuItemState = MenuItemState(MenuState.MENU_CATEGORIES, position, name, false)