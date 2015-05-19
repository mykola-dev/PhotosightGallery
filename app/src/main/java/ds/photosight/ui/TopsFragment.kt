package ds.photosight.ui


import android.support.v4.app.FragmentActivity
import ds.photosight.Constants
import ds.photosight.R

public class TopsFragment : ListFragmentAbstract() {

    override fun initList(): ListFragmentAbstract.ListAdapter {
        return ListFragmentAbstract.ListAdapter(getActivity(), R.layout.simple_list_item_activated_1, getResources().getStringArray(R.array.tops_array))
    }


    override fun getListType(): Int {
        return Constants.TAB_TOPS
    }
}

