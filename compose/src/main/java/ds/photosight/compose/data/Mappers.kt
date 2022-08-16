package ds.photosight.compose.data

import android.content.Context
import androidx.annotation.DrawableRes
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.screen.gallery.CategoryMenuItemState
import ds.photosight.parser.PhotoCategory
import ds.photosight.parser.PhotoDetails
import ds.photosight.parser.PhotoInfo

fun PhotoCategory.asUiModel(): CategoryMenuItemState = CategoryMenuItemState(index, name)

fun PhotoInfo.asUiModel(): Photo = Photo(id, thumb, large, pageUrl, title, authorName, authorUrl, paginationKey)

@DrawableRes
fun PhotoDetails.Award.asDrawableResource(context: Context): Int = context.resources.getIdentifier(this.toString(), "drawable", context.packageName)