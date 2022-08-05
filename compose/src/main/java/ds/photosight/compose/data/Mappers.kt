package ds.photosight.compose.data

import ds.photosight.compose.ui.model.CategoryMenuItemState
import ds.photosight.compose.ui.model.MenuState
import ds.photosight.compose.ui.model.Photo
import ds.photosight.parser.PhotoCategory
import ds.photosight.parser.PhotoDetails
import ds.photosight.parser.PhotoInfo

fun PhotoCategory.toMenuItemState(): CategoryMenuItemState = CategoryMenuItemState(index, name)

fun PhotoInfo.asUiModel(): Photo = Photo(id, thumb, large, pageUrl, title, authorName, authorUrl, paginationKey)