package ds.photosight.ui

import ds.photosight.Constants
import ds.photosight.R


public class CategoriesFragment : ListFragmentAbstract(), Constants {

    override fun initList(): ListFragmentAbstract.ListAdapter? =
        ListFragmentAbstract.ListAdapter(getActivity(), R.layout.simple_list_item_activated_1, getResources().getStringArray(R.array.categories_array))



    override fun getListType(): Int {
        return Constants.TAB_CATEGORIES
    }

}
