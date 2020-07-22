package ds.photosight.model

import ds.photosight.parser.PhotoCategory
import ds.photosight.parser.PhotoDetails
import ds.photosight.view.widget.VotesWidget
import ds.photosight.viewmodel.MenuItemState
import ds.photosight.viewmodel.MenuState

fun PhotoCategory.toMenuItemState(): MenuItemState = MenuItemState(MenuState.MENU_CATEGORIES, index, name, false)

fun PhotoDetails.Stats.asViewModel(): VotesWidget.Stats = VotesWidget.Stats(views, art, original, tech, likes, dislikes)