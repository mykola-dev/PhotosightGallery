package ds.photosight_legacy.ui


import ds.photosight.R
import ds.photosight_legacy.Constants

class TopsFragment : ListFragmentAbstract() {

    override fun initList(): ListAdapter {
        return ListAdapter(requireActivity(), R.layout.simple_list_item_activated_1, resources.getStringArray(R.array.ratings_array))
    }

    override val listType: Int
        get() = Constants.TAB_TOPS
}

