package ds.photosight.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import ds.photosight.Constants;
import ds.photosight.R;


public class AuthorsFragment extends ListFragmentAbstract {
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout list = (LinearLayout) inflater.inflate(R.layout.authors_list, null);
		return list;
	}

	@Override
	protected int getListType() {
		return Constants.TAB_AUTHORS;
	}

	@Override
	protected ListAdapter initList() {
		return null;
	}
	

}
