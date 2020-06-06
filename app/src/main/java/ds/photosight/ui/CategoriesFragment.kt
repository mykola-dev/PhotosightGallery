package ds.photosight.ui

import ds.photosight.Constants
import ds.photosight.R


class CategoriesFragment : ListFragmentAbstract(), Constants {

    override fun initList(): ListAdapter? =
            ListAdapter(activity!!, R.layout.simple_list_item_activated_1, resources.getStringArray(R.array.categories_array))


    override val listType: Int get() = Constants.TAB_CATEGORIES

}
