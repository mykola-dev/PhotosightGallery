package ds.photosight_legacy.ui

import ds.photosight.R
import ds.photosight_legacy.Constants


class CategoriesFragment : ListFragmentAbstract(), Constants {

    override fun initList(): ListAdapter? =
            ListAdapter(requireActivity(), R.layout.simple_list_item_activated_1, resources.getStringArray(R.array.categories_array))


    override val listType: Int get() = Constants.TAB_CATEGORIES

}
