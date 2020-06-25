package ds.photosight.model

import ds.photosight.parser.PhotoCategory
import ds.photosight.viewmodel.MenuItemState
import ds.photosight.viewmodel.MenuState

fun PhotoCategory.toMenuItemState(): MenuItemState = MenuItemState(MenuState.MENU_CATEGORIES, index, name, false)