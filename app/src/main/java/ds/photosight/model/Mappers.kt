package ds.photosight.model

import ds.photosight.parser.PhotoCategory
import ds.photosight.parser.PhotoDetails
import ds.photosight.ui.widget.VotesWidget
import ds.photosight.ui.viewmodel.CategoryMenuItemState

fun PhotoCategory.toMenuItemState(): CategoryMenuItemState = CategoryMenuItemState(index, name)

fun PhotoDetails.Stats.asViewModel(): VotesWidget.Stats = VotesWidget.Stats(views, art, original, tech, likes, dislikes)