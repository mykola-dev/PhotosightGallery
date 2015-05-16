package ds.photosight.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.widget.ImageView.ScaleType;
import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import de.greenrobot.event.EventBus;
import ds.photosight.App;
import ds.photosight.Constants;
import ds.photosight.R;
import ds.photosight.event.PhotoInfo;
import ds.photosight.model.ViewerData;
import ds.photosight.model.ViewerData.OnLoadListener;
import ds.photosight.utils.L;
import static ds.photosight.R.*;

public class ViewerFragment extends Fragment implements Constants, ViewPager.OnPageChangeListener {

	private static final int VIEWPAGER_ID = 123456789;
	private App app;
	private GridView grid;
	private View progress;
	private int currPage = 0;
	private int currPhoto = 0;
	private ViewerData viewerData;
	private ViewerPagerAdapter pageAdapter;
	private ViewPager viewPager;
	private int mColumnWidth;
	private AQuery aq;
	private List<Map<Integer, String>> tempdata;


	@Override
	public void onCreate(Bundle b) {

		super.onCreate(b);

	}


	private MainActivity getRoot() {
		return (MainActivity) getActivity();
	}


	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		L.v("viewer onActivity created");
	}


	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		L.v("Viewer view created!");

		aq = new AQuery(getActivity());
		currPage = getRoot().getCurrPage();

		/*if (App.getInstance().isPortrait())
			getRoot().toggleTabs(false);*/

		// setup title
		setTitle();

		setHasOptionsMenu(true);

		pageAdapter = new ViewerPagerAdapter(getActivity(), getNumOfPages());
		viewPager.setAdapter(pageAdapter);
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(currPage, false);
		//if (currPage == 0) {
		AQUtility.postDelayed(new Runnable() {
			@Override
			public void run() {
				initViews(getCurrentPage(), false);
			}
		}, 200);
		//}

	}


	@Override
	public void onResume() {
		super.onResume();
		L.v("viewer onresume");
		PhotoInfo info = (PhotoInfo) EventBus.getDefault().getStickyEvent(PhotoInfo.class);
		if (info != null) {
			//T.show(getActivity(), "got some event!");
			getRoot().setCurrentTab(info.tab);
			getRoot().setCurrPage(info.page);
			currPage = info.page;
			currPhoto = info.item;
			L.v("curr photo: " + currPhoto);
			tempdata = info.data;
			if (tempdata != null) {
				viewPager.setCurrentItem(currPage, false);
				AQUtility.postDelayed(new Runnable() {
					@Override
					public void run() {
						initViews(getCurrentPage(), false);
					}
				}, 200);
			}

			EventBus.getDefault().removeStickyEvent(info);
		}
	}


	private int getNumOfPages() {
		if (getRoot().getCurrentTab() == TAB_TOPS && getRoot().getListSelection(TAB_TOPS) != ITEM_TOP_DAY)
			return 1;
		else
			return 100500;
	}


	private void setTitle() {
		ActionBar ab = getRoot().getSupportActionBar();
		L.v("setting 2 line title");
		ab.setDisplayShowCustomEnabled(false);
		ab.setSubtitle(getResources().getString(R.string.page_) + (currPage + 1));
		String t;
		switch (getRoot().getCurrentTab()) {
			case TAB_CATEGORIES:
				t = getResources().getStringArray(array.categories_array)[getRoot().getCurrentListSelection()];
				break;
			case TAB_TOPS:
				t = getString(R.string.top_in) + getResources().getStringArray(array.tops_array)[getRoot().getCurrentListSelection()];
				break;
			default:
				t = null;
		}
		ab.setTitle(t.toUpperCase());
	}


	public void reload() {
		tempdata = null;
		pageAdapter = new ViewerPagerAdapter(getActivity(), getNumOfPages());
		viewPager.setAdapter(pageAdapter);
		viewPager.setOnPageChangeListener(this);
		currPage = getRoot().getCurrPage();
		viewPager.setCurrentItem(currPage, false);
		initViews(getCurrentPage(), false);

	}


	private void initViews(View v, boolean forceUpdate) {
		setTitle();
		grid = (GridView) v.findViewById(id.viewerGrid);
		int gridWidth = grid.getWidth();
		int numOfColumns = gridWidth / Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("thumbSize",
				getString(R.string.thumb_size_2)));
		mColumnWidth = gridWidth / numOfColumns;
		grid.setColumnWidth(mColumnWidth);
		grid.setNumColumns(numOfColumns);

		if (grid.getAdapter() != null && !forceUpdate) {
			L.w("grid.getAdapter() != null && !forceUpdate");
			if (currPhoto != 0) {
				grid.setSelection(currPhoto);
				//currPhoto = 0;
			}
			return;
		}
		progress = v.findViewById(id.viewerLoader);

		if (tempdata != null) {
			progress.setVisibility(View.GONE);
			loadThumbs(tempdata);
			tempdata = null;
			return;
		}

		progress.setVisibility(View.VISIBLE);
		grid.setVisibility(View.GONE);
		int tab = getRoot().getCurrentTab();
		viewerData = new ViewerData(tab, getRoot().getListSelection(tab), currPage);
		viewerData.setOnLoadListener(new OnLoadListener() {

			@Override
			public void onLoad(ArrayList<Map<Integer, String>> result, int page) {
				if (getActivity() == null)
					return;

				if (page != currPage)
					return;

				if (progress != null && getActivity() != null) {
					Animation a = AnimationUtils.loadAnimation(getActivity(), anim.fade_out);
					a.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(final Animation animation) {}


						@Override
						public void onAnimationEnd(final Animation animation) {
							progress.clearAnimation();
							progress.setVisibility(View.GONE);
						}


						@Override
						public void onAnimationRepeat(final Animation animation) { }
					});
					progress.startAnimation(a);
				}

				loadThumbs(result);

			}


			@Override
			public void onProgress(int page, int s) {
				if (page != currPage)
					return;
				//progress.setProgress(s);
			}


			@Override
			public void onProgressEnd(int page) {
				if (page != currPage)
					return;

			}
		});

	}


	private void loadThumbs(List<Map<Integer, String>> result) {
		final List<Map<Integer, String>> data = result;
		//progress.setVisibility(View.GONE);
		if (data == null) {
			if (getRoot().getCurrentTab() == TAB_TOPS && getRoot().getListSelection(TAB_TOPS) == ITEM_TOP_DAY)
				Toast.makeText(getActivity(), R.string.no_photos_try_next_page_, 1).show();
			else
				Toast.makeText(getActivity(), R.string.connection_error, 0).show();
			return;
		}
		grid.setVisibility(View.VISIBLE);
		GridAdapter adapter = new GridAdapter(getActivity(), result,mColumnWidth);
		grid.setAdapter(adapter);
		if (currPhoto != 0) {
			L.v("selecting grid cell");
			grid.setSelection(currPhoto);
			currPhoto = 0;
		}
		grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				Intent i = new Intent(getActivity(), GalleryActivity.class);
				// Bundle e;
				i.putExtra("data", (ArrayList) data);
				i.putExtra("item", pos);
				i.putExtra("page", currPage);
				i.putExtra("tab", getRoot().getCurrentTab());
				i.putExtra("category", getRoot().getCurrentListSelection());
				Log.d("startGallery", "item" + pos + " page" + currPage);
				startActivity(i);
				getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});

		registerForContextMenu(grid);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		viewPager = new ViewPager(getActivity());
		viewPager.setId(VIEWPAGER_ID);
		return viewPager;
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// if (menu == null)
		getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);

		super.onCreateContextMenu(menu, v, menuInfo);
	}


	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
		switch (item.getItemId()) {
			case R.id.imc_open_in_browser:
				showInBrowser(pos);
				break;
			case R.id.imc_show_detailed_info:
				showInfo(pos);
				// Toast.makeText(getActivity(), String.valueOf(Runtime.getRuntime().maxMemory()/1024/1024),1).show();
				break;
		}
		//
		return super.onContextItemSelected(item);
	}


	private void showInfo(int pos) {
		GridAdapter a = (GridAdapter) grid.getAdapter();
		DialogFragment newFragment = new InfoDialog(a.getData().get(pos));
		newFragment.show(getFragmentManager().beginTransaction(), "dialog");
	}


	private void showInBrowser(int pos) {
		Intent i;// = new Intent();
		i = new Intent(Intent.ACTION_VIEW);
		GridAdapter a = (GridAdapter) grid.getAdapter();
		if (pos != -1) {
			Log.d("#", "pos=" + pos);
			i.setData(Uri.parse(a.getData().get(pos).get(DATA_URL_PAGE)));
			startActivity(i);
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.im_refresh:
				initViews(getCurrentPage(), true);
				break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// if (menu.findItem(R.id.im_refresh) == null)
		inflater.inflate(R.menu.viewer_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}


	@Override
	public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) { }


	@Override
	public void onPageSelected(final int position) { }


	@Override
	public void onPageScrollStateChanged(final int state) {
		if (state == ViewPager.SCROLL_STATE_IDLE) {
			tempdata = null;  // reset temp data on page swap
			int pos = viewPager.getCurrentItem();

			currPage = pos;
			currPhoto = 0;
			getRoot().setCurrPage(currPage);
			initViews(getCurrentPage(), false);
			getRoot().getSupportActionBar().setSubtitle(getResources().getString(R.string.page_) + (currPage + 1));

			//Random r = new Random();
			L.v("page #" + pos);
		}
	}


	private View getCurrentPage() {
		return viewPager.findViewWithTag(viewPager.getCurrentItem());
	}


	//
	// **********************************************************************************************************************************************
	// ViewerPagerAdapter
	// **********************************************************************************************************************************************
	//
	public static class ViewerPagerAdapter extends PagerAdapter {

		private Context ctx;
		private int numOfPages = 0;


		public ViewerPagerAdapter(Context ctx, int numOfPages) {
			this.ctx = ctx;
			this.numOfPages = numOfPages;
		}


		@Override
		public int getCount() {
			return numOfPages;
		}


		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}


		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			L.v("item created " + position);
			View v = LayoutInflater.from(ctx).inflate(R.layout.viewer, null);
			v.setTag(position);
			container.addView(v);
			return v;
		}


		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}


	}


	//
	// **********************************************************************************************************************************************
	// GridAdapter
	// **********************************************************************************************************************************************
	//
	private static class GridAdapter extends BaseAdapter {

		private int cellSize;
		private Context ctx;
		int numOfItems;
		// private ArrayList<Map<Integer, String>> urlList = new ArrayList<Map<Integer, String>>();
		private AQuery aq;
		private List<Map<Integer, String>> data;
		private FrameLayout.LayoutParams imageLayout, progressLayout;
		private Animation fadeIn;


		public GridAdapter(Context ctx, List<Map<Integer, String>> result, int cellSize) {
			this.ctx = ctx;
			if (result == null)
				return;

			this.cellSize=cellSize;
			this.numOfItems = result.size();
			imageLayout = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			int d = cellSize / 5;
			progressLayout = new FrameLayout.LayoutParams(d, d);
			progressLayout.gravity = Gravity.CENTER;

			data = result;

			aq = new AQuery(ctx);
		}


		public List<Map<Integer, String>> getData() {
			return data;
		}


		@Override
		public int getCount() {
			return numOfItems;
		}


		@Override
		public Object getItem(int position) {
			return null;
		}


		@Override
		public long getItemId(int position) {
			return 0;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FrameLayout view = (FrameLayout) convertView;
			ViewHolder h;

			if (view == null) {
				view = new FrameLayout(ctx);
				view.setLayoutParams(new GridView.LayoutParams(cellSize, cellSize));

				h = new ViewHolder(view);
				view.setTag(h);
			} else {
				h = (ViewHolder) view.getTag();
				h.img.setImageBitmap(null);
			}


			if (!aq.shouldDelay(position, view, parent, data.get(position).get(DATA_URL_SMALL)))
				aq.id(h.img).progress(h.progress).image(data.get(position).get(DATA_URL_SMALL), true, true, 0, 0, null, AQuery.FADE_IN);
			else {
				h.img.setVisibility(View.GONE);
				h.progress.setVisibility(View.VISIBLE);
			}

			//imageLoader.DisplayImage(data.get(position), h.img, h.progress, false);
			return view;
		}


		private class ViewHolder {

			ImageView img;
			ProgressBar progress;


			public ViewHolder(ViewGroup view) {

				img = new ImageView(ctx);
				img.setScaleType(ScaleType.CENTER_CROP);
				img.setLayoutParams(imageLayout);
				//img.setAdjustViewBounds(true);

				progress = new ProgressBar(ctx);
				progress.setLayoutParams(progressLayout);

				view.addView(img);
				view.addView(progress);
			}

		}
	}

}
