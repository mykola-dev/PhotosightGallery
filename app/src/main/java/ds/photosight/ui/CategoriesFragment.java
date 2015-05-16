package ds.photosight.ui;

import ds.photosight.Constants;
import ds.photosight.R;


public class CategoriesFragment extends ListFragmentAbstract implements Constants {

	@Override
	protected ListAdapter initList() {
		return new ListAdapter(getActivity(), R.layout.simple_list_item_activated_1, getResources().getStringArray(
				R.array.categories_array));

	}


	@Override
	protected int getListType() {
		return TAB_CATEGORIES;
	}

}
