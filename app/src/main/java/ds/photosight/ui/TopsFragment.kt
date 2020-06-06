package ds.photosight.ui


import ds.photosight.Constants
import ds.photosight.R

class TopsFragment : ListFragmentAbstract() {

    override fun initList(): ListAdapter {
        return ListAdapter(activity!!, R.layout.simple_list_item_activated_1, resources.getStringArray(R.array.tops_array))
    }

    override val listType: Int
        get() = Constants.TAB_TOPS
}

