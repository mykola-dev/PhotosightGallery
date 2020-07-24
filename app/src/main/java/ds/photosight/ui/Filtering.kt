package ds.photosight.ui

import android.view.Menu
import ds.photosight.R
import ds.photosight.parser.CategoriesPhotosRequest
import ds.photosight.ui.viewmodel.PhotosFilter

interface PhotoFilterMapper<O, F : PhotosFilter?> {
    val filter: F
    val target: O

    fun fillTarget(): O
    fun generateModel(): F
}

class CategoriesFilterMapper(override val filter: PhotosFilter.Categories?, override val target: Menu) : PhotoFilterMapper<Menu, PhotosFilter.Categories?> {

    private val sortMapping = listOf(
        target.findItem(R.id.item_category_sort_time) to CategoriesPhotosRequest.SortTypeCategory.DEFAULT,
        target.findItem(R.id.item_category_sort_art) to CategoriesPhotosRequest.SortTypeCategory.ART,
        target.findItem(R.id.item_category_sort_orig) to CategoriesPhotosRequest.SortTypeCategory.ORIGINAL,
        target.findItem(R.id.item_category_sort_tech) to CategoriesPhotosRequest.SortTypeCategory.TECH,
        target.findItem(R.id.item_category_sort_rating) to CategoriesPhotosRequest.SortTypeCategory.COUNT,
        target.findItem(R.id.item_category_sort_comments) to CategoriesPhotosRequest.SortTypeCategory.COMMENTS_COUNT
    )
    private val filterMapping = listOf(
        target.findItem(R.id.item_category_filter_all) to CategoriesPhotosRequest.SortDumpCategory.ALL,
        target.findItem(R.id.item_category_filter_rating) to CategoriesPhotosRequest.SortDumpCategory.RATES

    )

    override fun fillTarget(): Menu {
        target.setGroupVisible(R.id.group_categories, filter?.enabled ?: false)
        listOf(
            sortMapping.first { it.second == filter?.sortTypeCategory },
            filterMapping.first { it.second == filter?.sortDumpCategory }
        ).forEach { it.first.isChecked = true }

        return target
    }

    override fun generateModel(): PhotosFilter.Categories? = PhotosFilter.Categories(
        enabled = true,
        sortDumpCategory = getFilterData(),
        sortTypeCategory = getSorterData()
    )
        .takeIf { filter != it }  // prevent false triggering

    private fun getFilterData(): CategoriesPhotosRequest.SortDumpCategory = filterMapping
        .first { it.first.isChecked }
        .second

    private fun getSorterData(): CategoriesPhotosRequest.SortTypeCategory = sortMapping
        .first { it.first.isChecked }
        .second
}

