package ds.photosight.viewmodel

import androidx.paging.PagingData
import ds.photosight.parser.BestPhotosRequest
import ds.photosight.parser.CategoriesPhotosRequest
import ds.photosight.parser.PhotoInfo
import kotlin.coroutines.CoroutineContext

data class MenuItemState(
    val menu: Int,
    val id: Int,
    val title: String,
    var isSelected: Boolean
)

data class MenuState(
    val categories: List<MenuItemState>,
    val ratings: List<MenuItemState>,
    val categoriesFilter: CategoriesFilter = CategoriesFilter(true)
) {
    companion object {
        const val MENU_CATEGORIES = 0
        const val MENU_RATINGS = 1
    }

}

/*data class PhotosState(
    val photos:  = PagingData.empty()
    //val currentPage: Int = 1,
    //val isLoading: Boolean = true,

)*/

//typealias PhotoMap = Map<Int, PhotoInfo>

interface PhotosFilter {
    val enabled: Boolean
}

data class CategoriesFilter(
    override val enabled: Boolean,
    val sortDumpCategory: CategoriesPhotosRequest.SortDumpCategory = CategoriesPhotosRequest.SortDumpCategory.ALL,
    val sortTypeCategory: CategoriesPhotosRequest.SortTypeCategory = CategoriesPhotosRequest.SortTypeCategory.DEFAULT
) : PhotosFilter

data class BestPhotosFilter(
    override val enabled: Boolean,
    val sort: BestPhotosRequest.Sort = BestPhotosRequest.Sort.BEST,
    val time: BestPhotosRequest.Time = BestPhotosRequest.Time.DAY
) : PhotosFilter

data class DailyPhotosFilter(
    override val enabled: Boolean,
    val category: Int? = null
) : PhotosFilter


