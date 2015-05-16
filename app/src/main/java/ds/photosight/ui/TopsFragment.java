package ds.photosight.ui;


import ds.photosight.Constants;
import ds.photosight.R;

public class TopsFragment extends ListFragmentAbstract {

	@Override
	protected ListAdapter initList() {
		return new ListAdapter(getActivity(), R.layout.simple_list_item_activated_1, getResources().getStringArray(R.array.tops_array));
	}


	@Override
	protected int getListType() {
		return Constants.TAB_TOPS;
	}
}
