package ds.photosight.compose.data

import ds.photosight.compose.ui.model.CategoryMenuItemState
import ds.photosight.compose.ui.model.MenuState
import ds.photosight.parser.PhotoCategory
import ds.photosight.parser.PhotoDetails

fun PhotoCategory.toMenuItemState(): CategoryMenuItemState = CategoryMenuItemState(index, name)

// fun PhotoDetails.Stats.asViewModel(): VotesWidget.Stats = VotesWidget.Stats(views, art, original, tech, likes, dislikes)