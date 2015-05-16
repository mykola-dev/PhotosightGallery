package ds.photosight.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.flurry.android.FlurryAgent;
import ds.photosight.Constants;
import ds.photosight.R;
import ds.photosight.utils.L;

public abstract class ListFragmentAbstract extends ListFragment implements Constants {

	protected int mPositionChecked = 0;
	protected int mPositionShown = -1;


	private boolean isActive() {
		return getUserVisibleHint();
	}


	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		L.v("list on view created " + getListType());

		mPositionChecked = getRoot().getListSelection(getListType());
		if (Build.VERSION.SDK_INT > 8)
			getListView().setOverScrollMode(View.OVER_SCROLL_NEVER);

		ArrayAdapter<String> adapter = initList();
		setListAdapter(adapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getListView().setSelection(mPositionChecked);
		getListView().setItemChecked(mPositionChecked, true);
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		showDetails(position);

		flurry(((TextView) v.findViewById(android.R.id.text1)).getText().toString());

		super.onListItemClick(l, v, position, id);
	}


	private void flurry(String text) {
		Map<String, String> args = new HashMap<String, String>();
		args.put("Category", text);
		args.put("Tab", String.valueOf(getListType()));
		FlurryAgent.logEvent(LIST_CLICKED, args);
		Log.d(args.get("Tab"), args.get("Category"));
	}


	@Override
	public void onSaveInstanceState(Bundle b) {
		/*b.putInt("item", mPositionShown);
		if (!App.getInstance().isPortrait())
			b.putBoolean("fromLandscape", true);*/
		super.onSaveInstanceState(b);
	}


	private MainActivity getRoot() {
		return (MainActivity) getActivity();
	}


	void showDetails(int index) {
		if (!isActive())
			return;

		mPositionChecked = index;
		getListView().setItemChecked(index, true);

		getRoot().setCurrPage(0);
		getRoot().selectItem(index);

		mPositionShown = index;
	}


	protected abstract int getListType();


	protected abstract ListAdapter initList();


	//
	// **********************************************************************************************************************************************
	// ListAdapter
	// **********************************************************************************************************************************************
	//
	public class ListAdapter extends ArrayAdapter<String> {

		public ListAdapter(Context context, int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);

			if (position == mPositionChecked) {
				v.setBackgroundResource(R.drawable.selector_color_solid);
			} else {
				v.setBackgroundResource(R.drawable.list_selector);
			}

			return v;
		}

	}
}
