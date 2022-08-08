package ds.photosight.compose.data

import ds.photosight.compose.ui.model.CategoryMenuItemState
import ds.photosight.compose.ui.model.Photo
import ds.photosight.parser.PhotoCategory
import ds.photosight.parser.PhotoInfo

fun PhotoCategory.toMenuItemState(): CategoryMenuItemState = CategoryMenuItemState(index, name)

fun PhotoInfo.asUiModel(idx: Int): Photo = Photo(idx, id, thumb, large, pageUrl, title, authorName, authorUrl, paginationKey)