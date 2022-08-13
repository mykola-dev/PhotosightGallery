package ds.photosight.compose.data

import android.content.Context
import androidx.annotation.DrawableRes
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.model.PhotoStats
import ds.photosight.compose.ui.screen.gallery.CategoryMenuItemState
import ds.photosight.parser.PhotoCategory
import ds.photosight.parser.PhotoDetails
import ds.photosight.parser.PhotoInfo

fun PhotoCategory.asUiModel(): CategoryMenuItemState = CategoryMenuItemState(index, name)

fun PhotoInfo.asUiModel(idx: Int): Photo = Photo(idx, id, thumb, large, pageUrl, title, authorName, authorUrl, paginationKey)

fun PhotoDetails.Stats.asUiModel(): PhotoStats = PhotoStats(views, art, original, tech, likes, dislikes)

@DrawableRes
fun PhotoDetails.Award.asDrawableResource(context: Context): Int = context.resources.getIdentifier(this.toString(), "drawable", context.packageName)