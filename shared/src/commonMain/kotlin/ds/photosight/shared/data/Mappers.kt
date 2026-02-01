package ds.photosight.shared.data

import ds.photosight.shared.ui.model.Photo
import ds.photosight.shared.ui.model.Category
import ds.photosight.shared.ui.screen.gallery.CategoryMenuItemState
import ds.photosight.parser.CategoriesPhotosRequest
import ds.photosight.parser.PhotoCategory
import ds.photosight.parser.PhotoDetails
import ds.photosight.parser.PhotoInfo

fun PhotoCategory.asUiModel(): CategoryMenuItemState? {
    val category = Category.fromId(index) ?: return null
    return CategoryMenuItemState(category)
}

fun PhotoInfo.asUiModel(paginationKey: String?): Photo = Photo(
    id = id,
    thumb = thumb,
    large = large,
    pageUrl = pageUrl,
    title = title,
    authorName = authorName,
    authorUrl = authorUrl,
    paginationKey = paginationKey
)

fun CategoriesPhotosRequest.FilterDumpCategory.getTitleId(): String = when (this) {
    CategoriesPhotosRequest.FilterDumpCategory.ALL -> "all_authors"
    CategoriesPhotosRequest.FilterDumpCategory.RATES -> "rating_authors"
}

fun CategoriesPhotosRequest.SortTypeCategory.getTitleId(): String = when (this) {
    CategoriesPhotosRequest.SortTypeCategory.DEFAULT -> "by_date"
    CategoriesPhotosRequest.SortTypeCategory.COUNT -> "by_rating"
    CategoriesPhotosRequest.SortTypeCategory.COMMENTS_COUNT -> "by_comments"
    CategoriesPhotosRequest.SortTypeCategory.ART -> "by_artistry"
    CategoriesPhotosRequest.SortTypeCategory.ORIGINAL -> "by_originality"
    CategoriesPhotosRequest.SortTypeCategory.TECH -> "by_technique"
}
