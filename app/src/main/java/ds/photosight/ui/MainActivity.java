package ds.photosight.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.astuetz.PagerSlidingTabStrip;
import com.flurry.android.FlurryAgent;
import ds.photosight.App;
import ds.photosight.Constants;
import ds.photosight.R;
import ds.photosight.utils.L;
import ds.photosight.utils.Utils;


public class MainActivity extends AppCompatActivity implements Constants, ViewPager.OnPageChangeListener,
		android.support.v4.app.FragmentManager.OnBackStackChangedListener {

	private ViewPager mViewPager;
	private TabsPagerAdapter mPagerAdapter;
	private ViewerFragment mViewerFragment;
	private boolean isInViewer;                 // is viewer opened
	private int currTab;                     // currently selected list
	PagerSlidingTabStrip tabs;


	public int getCurrPage() {
		return currPage;
	}


	public void setCurrPage(final int currPage) {
		this.currPage = currPage;
	}


	private int currPage;
	private int[] mListSelects = new int[3];    // choises of each list


	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.main);


		if (b != null) {
			isInViewer = b.getBoolean("isInViewer", false);
			currTab = b.getInt("tab");
			mListSelects = b.getIntArray("selects");
			currPage = b.getInt("page");
		} else {
		}

		getSupportFragmentManager().addOnBackStackChangedListener(this);
		ActionBar bar = getSupportActionBar();

		// tabs
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mPagerAdapter = new TabsPagerAdapter(this);
		mViewPager.setAdapter(mPagerAdapter);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.navigation);
		tabs.setViewPager(mViewPager);
		tabs.setOnPageChangeListener(this);
		//tabs.setTextColorResource(R.color.tabs);
		mViewPager.setCurrentItem(currTab);

		if (App.getInstance().isPortrait()) {
			L.v("portrait main activity init. isInViewer?" + isInViewer);

			if (isInViewer) {
				//injectFragment(getViewerFragment(), R.id.main_layout, true);
				selectItem(mListSelects[currTab]);
				L.v("switched to viewer fragment");
			}
			toggleTabs(!isInViewer);


		} else {
			L.v("landscape main activity init");
			selectItem(mListSelects[currTab]);
			isInViewer = true;
			toggleTabs(true);
		}


		// Check if app version is different
		showAbout();

	}


	public ViewerFragment getViewerFragment() {
		ViewerFragment f = (ViewerFragment) getSupportFragmentManager().findFragmentByTag(ViewerFragment.class.getName());
		//if (f == null)
		//f = new ViewerFragment();

		return f;
	}


	public void injectFragment(Fragment what, int where, boolean backStack) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(where, what, ((Object) what).getClass().getName());
		if (backStack)
			ft.addToBackStack(null);
		ft.commit();
		getSupportFragmentManager().executePendingTransactions();
	}


	private void addTabs() {


	}


	public void selectItem(int item) {
		L.v("item selected " + item);
		L.v("portrait? " + App.getInstance().isPortrait());
		//
		mListSelects[currTab] = item;
		mViewerFragment = getViewerFragment();
		if (App.getInstance().isPortrait()) {
			if (mViewerFragment == null) {
				L.v("viewer not found in the manager. creating new one");
				mViewerFragment = new ViewerFragment();
				injectFragment(mViewerFragment, R.id.main_layout, true);
			} else {
				L.w("viewer found in manager. in portrait mode. weird!");

				getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).remove(mViewerFragment).commit();
				getSupportFragmentManager().executePendingTransactions();
				injectFragment(mViewerFragment, R.id.main_layout, true);

			}
		} else {
			if (mViewerFragment == null) {
				L.v("viewer not found in the manager. creating new one");
				mViewerFragment = new ViewerFragment();
				injectFragment(mViewerFragment, R.id.viewer_frame, false);
			} else {
				L.v("viewer should be already in the place. need just invalidate it!");
				//setCurrPage(0);
				mViewerFragment.reload();
			}
		}
	}


	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		//getSupportFragmentManager().popBackStackImmediate();
		super.onConfigurationChanged(newConfig);
	}


	private void setTitle() {
		L.v("setting default title");
		getSupportActionBar().setTitle(R.string.photosight);
		getSupportActionBar().setSubtitle(null);
	}


	public void setCurrentTab(final int currentTab) {
		currTab = currentTab;
	}


	public void setListSelection(int list, int selection) {
		mListSelects[list] = selection;
	}


	public int getCurrentTab() { return currTab; }


	public int getListSelection(int list) { return mListSelects[list]; }


	public int getCurrentListSelection() { return mListSelects[currTab]; }


	@Override
	protected void onSaveInstanceState(final Bundle b) {
		//super.onSaveInstanceState(b);
		b.putInt("tab", currTab);
		b.putBoolean("isInViewer", isInViewer);
		b.putIntArray("selects", mListSelects);
		b.putInt("page", currPage);
	}


	private void showAbout() {
		String ver;
		try {
			PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);
			ver = manager.versionName;
		} catch (NameNotFoundException e1) {
			ver = "100500";
		}
		if (!App.getPrefs().getString("version", "0").equals(ver)) {
			App.getPrefs().edit().putString("version", ver).commit();
			App.showAbout(this);
		}

	}


	private void addBanner() {
		/*ad = (AdView) findViewById(R.id.admob);
		AdManager.initAdmobMediationBanner(ad);*/
	}


	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY);
	}


	@Override
	protected void onPostResume() {
		super.onPostResume();
		invalidateOptionsMenu();
	}


	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);

	}


	@Override
	protected void onDestroy() {
		/*if (ad != null)
			ad.destroy();*/

		getSupportFragmentManager().removeOnBackStackChangedListener(this);

		super.onDestroy();
	}


	@Override
	public void onBackPressed() {
		int c = getSupportFragmentManager().getBackStackEntryCount();
		L.v("back stack amount:" + c);
		if (App.getPrefs().getBoolean(PREFS_KEY_AUTO_CLEAR_CACHE, true) && c == 0) {
			L.d("clearing caches...");
			Utils.clearCaches(this);
		}

		super.onBackPressed();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;

		switch (item.getItemId()) {

			case R.id.im_settings:
				i = new Intent(getApplicationContext(), PreferencesActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				break;
			case R.id.im_open_photosight:
				i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(URL_MAIN));
				startActivity(i);
				break;
			case R.id.im_about:
				App.showAbout(this);
				break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onBackStackChanged() {
		L.v("on back stack!");
		int c = getSupportFragmentManager().getBackStackEntryCount();
		isInViewer = c != 0;
		toggleTabs(!isInViewer);
	}


	public void toggleTabs(boolean enable) {
		if (enable) {
			tabs.setVisibility(View.VISIBLE);
			if (App.getInstance().isPortrait())
				setTitle();
		} else
			tabs.setVisibility(View.GONE);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//L.v("menu");
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}


	@Override
	public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) { }


	@Override
	public void onPageSelected(final int position) {
		L.v("ON PAGE selected");
		currTab = position;
	}


	@Override
	public void onPageScrollStateChanged(final int state) {

	}


	//
	// **********************************************************************************************************************************************
	// TabsPagerAdapter
	// **********************************************************************************************************************************************
	//
	public static class TabsPagerAdapter extends FragmentPagerAdapter {

		private final Context ctx;


		public TabsPagerAdapter(FragmentActivity a) {
			super(a.getSupportFragmentManager());
			ctx = a;
		}


		@Override
		public int getCount() {
			return 2;
		}


		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return Fragment.instantiate(ctx, CategoriesFragment.class.getName(), null);
				case 1:
					return Fragment.instantiate(ctx, TopsFragment.class.getName(), null);
				default:
					return null;

			}
		}


		@Override
		public CharSequence getPageTitle(final int position) {
			switch (position) {
				case 0:
					return ctx.getString(R.string.categories);
				case 1:
					return ctx.getString(R.string.top);
				default:
					return super.getPageTitle(position);

			}
		}
	}
}