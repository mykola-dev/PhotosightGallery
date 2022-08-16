package ds.photosight.compose.data

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ds.photosight.compose.R
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.screen.gallery.CategoryMenuItemState
import ds.photosight.parser.CategoriesPhotosRequest
import ds.photosight.parser.PhotoCategory
import ds.photosight.parser.PhotoDetails
import ds.photosight.parser.PhotoInfo

fun PhotoCategory.asUiModel(): CategoryMenuItemState = CategoryMenuItemState(index, name)

fun PhotoInfo.asUiModel(): Photo = Photo(id, thumb, large, pageUrl, title, authorName, authorUrl, paginationKey)

@DrawableRes
fun PhotoDetails.Award.asDrawableResource(context: Context): Int = context.resources.getIdentifier(this.toString(), "drawable", context.packageName)

@StringRes
fun CategoriesPhotosRequest.FilterDumpCategory.getTitleId() = when (this) {
    CategoriesPhotosRequest.FilterDumpCategory.ALL -> R.string.all_authors
    CategoriesPhotosRequest.FilterDumpCategory.RATES -> R.string.rating_authors
}

@StringRes
fun CategoriesPhotosRequest.SortTypeCategory.getTitleId() = when (this) {
    CategoriesPhotosRequest.SortTypeCategory.DEFAULT -> R.string.by_date
    CategoriesPhotosRequest.SortTypeCategory.COUNT -> R.string.by_rating
    CategoriesPhotosRequest.SortTypeCategory.COMMENTS_COUNT -> R.string.by_comments
    CategoriesPhotosRequest.SortTypeCategory.ART -> R.string.by_artistry
    CategoriesPhotosRequest.SortTypeCategory.ORIGINAL -> R.string.by_originality
    CategoriesPhotosRequest.SortTypeCategory.TECH -> R.string.by_technique
}