package ds.photosight.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import de.greenrobot.event.EventBus;
import ds.photosight.App;
import ds.photosight.Constants;
import ds.photosight.R;
import ds.photosight.event.PhotoInfo;
import ds.photosight.model.AdvancedInfoParser;
import ds.photosight.model.Comment;
import ds.photosight.model.ViewerData;
import ds.photosight.model.ViewerData.OnLoadListener;
import ds.photosight.ui.widget.GalleryViewPager;
import ds.photosight.ui.widget.VotesWidget;
import ds.photosight.utils.L;
import ds.photosight.utils.ShareProgress;
import ds.photosight.utils.Utils;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

//
//**********************************************************************************************************************************************
// GalleryActivity
//**********************************************************************************************************************************************
//
public class GalleryActivity extends AppCompatActivity implements Constants, OnPageChangeListener {

	private final static long ACTION_BAR_DELAY = 5000;

	private GalleryViewPager pager;
	private GalleryAdapter pagerAdapter;
	private List<Map<Integer, String>> data;
	private Map<Integer, List<Map<Integer, String>>> dataSet = new HashMap<>();
	private int currPhoto;
	private int currPage;
	private int currTab;
	private int currCategory;
	private int offset;
	private int adCounter = 0;
	private final static int AD_FREQ = 2;
	//private InterstitialAd interstitial;
	private AQuery aq;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private Map<String, List<Comment>> mComments;
	private Map<String, List<String>> mAwards;
	private Map<String, List<Integer>> mRates;
	private VotesWidget mVotesView;

	public static boolean actionBarAutoHide = true;


	@Override
	protected void onCreate(Bundle state) {

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(state);

		String l = Locale.getDefault().getISO3Language();
		Log.d("#", "locale=" + l);

		aq = new AQuery(this);

		if (Build.VERSION.SDK_INT >= 11)
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

		// setContentView(R.layout.gallery);
		Bundle b = getIntent().getExtras();
		if (state != null) {
			data = (ArrayList) state.getSerializable("data");
			currPage = state.getInt("page");
			currPhoto = state.getInt("item");
			currTab = state.getInt("tab");
			currCategory = state.getInt("category");
			ArrayList comments = (ArrayList) state.getSerializable("comments");
			ArrayList rates = (ArrayList) state.getSerializable("rates");
			ArrayList awards = (ArrayList) state.getSerializable("awards");
			if (comments != null) {
				mComments = new HashMap<>();
				mComments.put(getCurrentItem().get(DATA_URL_PAGE), comments);
				mAwards = new HashMap<>();
				mAwards.put(getCurrentItem().get(DATA_URL_PAGE), awards);
				mRates = new HashMap<>();
				mRates.put(getCurrentItem().get(DATA_URL_PAGE), rates);
			}
		} else {
			data = (ArrayList<Map<Integer, String>>) b.get("data");
			currTab = b.getInt("tab");
			currPage = b.getInt("page");
			currPhoto = b.getInt("item");
			currCategory = b.getInt("category");
		}

		String t = "";
		switch (currTab) {
			case TAB_CATEGORIES:
				t = getResources().getStringArray(R.array.categories_array)[currCategory];
				break;
			case TAB_TOPS:
				t = getString(R.string.top_in) + getResources().getStringArray(R.array.tops_array)[currCategory];
		}


		setContentView(R.layout.gallery);
		pager = (GalleryViewPager) findViewById(R.id.pager);//new ds.photosight.ui.widget.GalleryViewPager(this);
		pager.setOnPageChangeListener(this);
		//pager.setOnClickListener(mPagerClickListener);


		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				actionBarAutoHide = true;
				aq.id(R.id.drawer_progress).visible();
				aq.id(R.id.drawer_list).gone();
				getSupportActionBar().hide();
			}


			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				loadComments();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		bar.setIcon(R.drawable.icon_white2);
		bar.setDisplayShowCustomEnabled(true);
		bar.setCustomView(R.layout.ab_title2);
		refreshTitle(t);
		bar.setBackgroundDrawable(new ColorDrawable(0x55000000));
		bar.setSplitBackgroundDrawable(new ColorDrawable(0x55000000));
		bar.hide();

		initPager(currPhoto);


	}


	private void showComments(List<Comment> comments, final List<Integer> rates, final List<String> avards) {
		aq.id(R.id.drawer_progress).gone();
		aq.id(R.id.drawer_list).visible();
		if (aq.getListView().getHeaderViewsCount() == 0) {
			//final View header = new View(this);
			/*AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Utils.dp(48));
			header.setLayoutParams(params);
			aq.getListView().addHeaderView(header);*/
			aq.getListView().setHeaderDividersEnabled(false);

			mVotesView = (VotesWidget) getLayoutInflater().inflate(R.layout.votes, null);
			aq.getListView().addHeaderView(mVotesView);

		}

		mVotesView.setRates(rates);
		mVotesView.setAvards(avards);
		mVotesView.runAnimations();
		aq.adapter(new CommentsAdapter(this, comments));
		//aq.getListView().setDivider(new DottedDivider());

	}


	private void loadComments() {
		actionBarAutoHide = false;
		getSupportActionBar().hide();
		final String url = getCurrentItem().get(DATA_URL_PAGE);
		if (mComments == null) {
			mComments = new HashMap<>();
			mAwards = new HashMap<>();
			mRates = new HashMap<>();
		}
		if (mComments.containsKey(url)) {
			showComments(mComments.get(url), mRates.get(url), mAwards.get(url));
			return;
		}

		L.v("loading url: " + url);
		new AdvancedInfoParser().parseAsync(url, new AdvancedInfoParser.Callback() {

			@Override
			public void onDone(final List<Comment> comments, final List<Integer> rates, final List<String> awards) {
				L.v("done " + (comments != null ? comments.size() : "null"));
				mComments.put(url, comments);
				mRates.put(url, rates);
				mAwards.put(url, awards);
				if (getCurrentItem().get(DATA_URL_PAGE).equals(url)) {
					showComments(comments, rates, awards);
				}
			}
		});

	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
		if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
			loadComments();
			L.v("drawer is opened");
		}
	}


	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);

	}


	@Override
	protected void onDestroy() {

		super.onDestroy();
	}


	@Override
	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		state.putInt("page", currPage);
		state.putInt("item", currPhoto);
		state.putInt("tab", currTab);
		state.putSerializable("data", (ArrayList) data);
		state.putSerializable("offset", offset);

		String url = getCurrentItem().get(DATA_URL_PAGE);
		if (mComments != null && mComments.containsKey(url)) {
			state.putSerializable("comments", (ArrayList) mComments.get(url));
			state.putSerializable("rates", (ArrayList) mRates.get(url));
			state.putSerializable("awards", (ArrayList) mAwards.get(url));
		}

		state.putInt("category", currCategory);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.gallery_menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}


		switch (item.getItemId()) {
			case R.id.im_share_link:
				showShareLink();
				break;
			case R.id.im_share_img:
				showShareImage();
				break;
			case R.id.im_save_sdcard:

				saveImage();
				showAd();
				break;
			case R.id.im_open_in_browser:
				i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(getCurrentItem().get(DATA_URL_PAGE)));
				startActivity(i);
				break;
			case R.id.im_info:
				showInfo();
				break;
			case R.id.im_wallpaper:
				setWallpaper2();
				break;
			/*case android.R.id.home:
				closeActivity();
				break;*/

		}
		return super.onOptionsItemSelected(item);
	}


	private void setWallpaper2() {

		Uri uri = Uri.parse(getCurrentItem().get(DATA_URL_LARGE));
		File f = aq.makeSharedFile(uri.toString(), uri.getLastPathSegment());
		Intent i = new Intent(Intent.ACTION_ATTACH_DATA);
		i.setDataAndType(Uri.fromFile(f), "image/*");
		i.putExtra("mimeType", "image/*");
		i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
		startActivity(Intent.createChooser(i, getString(R.string.set_as)));
	}


	private Map<Integer, String> getCurrentItem() {
		return data.get(currPhoto);
	}


	private void showInfo() {
		DialogFragment newFragment = new InfoDialog(getCurrentItem());
		newFragment.show(getSupportFragmentManager().beginTransaction(), "dialog");
	}


	private void saveImage() {
		File path = new File(Environment.getExternalStorageDirectory(), App.getPrefs().getString(PREFS_KEY_SAVE_PATH, PATH_SAVED_DEFAULT));
		String message;
		Uri uri = Uri.parse(getCurrentItem().get(DATA_URL_LARGE));
		String filename = uri.getLastPathSegment();
		File src = aq.getCachedFile(uri.toString());
		L.v("saving " + uri.toString());
		File dst = new File(path, filename);
		if (src != null) {
			boolean b = Utils.copyFiles(src, dst);
			if (b)
				message = getString(R.string.saved_to_) + path;
			else {
				message = getString(R.string.failed);
				L.e("file copying filed");
			}
		} else {
			message = getString(R.string.failed);
			L.e("cached image not found!");
		}

		Toast.makeText(this, message, 0).show();
	}


	private void showAd() {
		if (PRO_VERSION)
			return;

	}


	private void showShareLink() {
		final Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subj));
		share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + " " + getCurrentItem().get(DATA_URL_PAGE));
		new ShareProgress(this).execute(share);

	}


	private void showShareImage() {
		Uri uri = Uri.parse(getCurrentItem().get(DATA_URL_LARGE));
		File file = aq.makeSharedFile(uri.toString(), uri.getLastPathSegment());
		final Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/*");
		share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subj));
		share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
		share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		new ShareProgress(this).execute(share);

	}


	private boolean needAd() {
		adCounter++;
		if (adCounter % AD_FREQ == 0 && offset == 1)
			return true;
		return false;
	}


	private void loadNewPage() {
		Toast.makeText(this, R.string.loading_next_page, 0).show();

		// final int newItem;
		if (!dataSet.containsKey(currPage)) {
			dataSet.put(currPage, data);
		}

		final int direction = (currPhoto + offset) == 0 ? -1 : 1;
		currPage += direction;

		if (!dataSet.containsKey(currPage)) {
			ViewerData viewerData;
			viewerData = new ViewerData(currTab, currCategory, currPage);
			viewerData.setOnLoadListener(new OnLoadListener() {

				@Override
				public void onLoad(ArrayList<Map<Integer, String>> result, int page) {
					data = result;
					currPhoto = direction == -1 ? data.size() - 1 : 0;
					initPager(currPhoto);
					if (needAd()) {
						showAd();
					}
				}


				@Override
				public void onProgress(int page, int progress) { }


				@Override
				public void onProgressEnd(int page) { }

			});
		} else {
			data = dataSet.get(currPage);
			currPhoto = direction == -1 ? data.size() - 1 : 0;
			initPager(currPhoto);
		}

	}


	private void initPager(int item) {
		if (data == null) {
			Toast.makeText(this, R.string.connection_error, 0).show();
			return;
		}
		offset = currPage == 0 ? 0 : 1;

		pagerAdapter = new GalleryAdapter(this, data, data.size() + offset + 1);
		pager.setAdapter(pagerAdapter);
		pager.setCurrentItem(item + offset, false);
		refreshTitle(null);
	}


	private void closeActivity() {
		//T.show(this,"prepare event...");
		EventBus.getDefault().postSticky(new PhotoInfo(currTab, currCategory, currPage, currPhoto, data));
		//finish();
	}


	@Override
	public void onBackPressed() {
		closeActivity();
		super.onBackPressed();
	}


	@Override
	public void onPageScrollStateChanged(int state) {
		if (state == ViewPager.SCROLL_STATE_IDLE) {
			int pos = pager.getCurrentItem();
			currPhoto = pos - offset;
			if ((pos == 0 && currPage != 0) || pos == data.size() + offset) {
				loadNewPage();
				return;
			}

			refreshTitle(null);
		}
	}


	private void refreshTitle(String title) {
		AQuery aq = new AQuery(getSupportActionBar().getCustomView());
		if (title != null)
			aq.id(R.id.text1).text(title);
		aq.id(R.id.text2).text(String.format("[%s:%s] %s", currPage + 1, currPhoto + 1, getCurrentItem().get(DATA_IMG_NAME)));

	}


	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) { }


	@Override
	public void onPageSelected(int pos) { }



	//
	// **********************************************************************************************************************************************
	// GalleryAdapter
	// **********************************************************************************************************************************************
	//
	public static class GalleryAdapter extends PagerAdapter {

		private int numOfItems;
		// private String[] links;
		private Context ctx;
		private AQuery aq;
		private List<Map<Integer, String>> data;
		private View mCurrentView;
		private int offset;
		private int mImageWidth;
		private PhotoViewAttacher mAttacher;


		private GalleryViewPager.ClickListener mPagerClickListener = new GalleryViewPager.ClickListener() {

			Handler h = new Handler();
			Runnable r = new Runnable() {

				@Override
				public void run() {
					hide(((AppCompatActivity) ctx).getSupportActionBar());
				}
			};


			@Override
			public void onClick() {
				L.v("onClick");
				final ActionBar ab = ((AppCompatActivity) ctx).getSupportActionBar();
				if (!ab.isShowing()) {
					show(ab);
					h.postDelayed(r, ACTION_BAR_DELAY);
				} else {
					h.removeCallbacks(r);
					hide(ab);

				}
			}


			private void show(ActionBar ab) {
				ab.show();
				if (Build.VERSION.SDK_INT >= 11)
					((AppCompatActivity) ctx).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			}


			private void hide(ActionBar ab) {
				ab.hide();
				if (Build.VERSION.SDK_INT >= 11)
					((AppCompatActivity) ctx).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
			}
		};
		//private int failCount = 0;


		private class OnDoubleTapListener implements GestureDetector.OnDoubleTapListener {

			PhotoView photo;


			private OnDoubleTapListener(final PhotoView photo) {
				this.photo = photo;
			}


			@Override
			public boolean onSingleTapConfirmed(final MotionEvent e) {
				L.v("onSingleTapConfirmed");
				mPagerClickListener.onClick();
				return false;
			}


			@Override
			public boolean onDoubleTap(final MotionEvent e) {
				L.v("onDoubleTap");

				if (photo == null)
					return false;

				try {
					float scale;// = photo.getScale();
					float x = e.getX();
					float y = e.getY();

					RectF rect = photo.getDisplayRect();
					int w = (int) rect.width();
					int h = (int) rect.height();
					L.v("w=" + w + " h=" + h);

					if (w < photo.getWidth())
						scale = (float) photo.getWidth() / w;
					else if (h < photo.getHeight())
						scale = (float) photo.getHeight() / h;
					else scale = photo.getMinimumScale();

					photo.setScale(scale, x, y, true);

				} catch (ArrayIndexOutOfBoundsException ex) {
					// Can sometimes happen when getX() and getY() is called
					ex.printStackTrace();
				}

				return true;
			}


			@Override
			public boolean onDoubleTapEvent(final MotionEvent e) {
				L.v("onDoubleTapEvent");
				return false;
			}
		}


		public GalleryAdapter(Context c, List<Map<Integer, String>> data, int size) {
			ctx = c;
			numOfItems = size;
			aq = new AQuery(c);
			this.data = data;
			offset = size - data.size() - 1;
			mImageWidth = App.getPrefs().getBoolean("rescale", false) ? RESCALE_WIDTH : 0;

		}


		@Override
		public Object instantiateItem(ViewGroup container, int pos) {
			int position = pos - offset;

			View v = LayoutInflater.from(ctx).inflate(R.layout.gallery_fragment, null);
			container.addView(v, 0);

			if (((offset == 1) && pos == 0) || pos >= numOfItems - 1) {
				return v;
			}

			final Map<Integer, String> item = data.get(position);
			aq.recycle(v);
			final Bitmap thumb = aq.getCachedImage(item.get(DATA_URL_SMALL));
			String url = item.get(DATA_URL_LARGE);
			L.v("img url=" + url);
			PhotoView img = (PhotoView) aq.id(R.id.gallery_image).getView();

			img.setOnDoubleTapListener(new OnDoubleTapListener(img));
			img.setMaximumScale(10);

			/*int fade;
			if (thumb != null) {
				L.v("thumb found");
				fade = 0;
			} else {
				fade = AQuery.FADE_IN;
				L.w("no thumb. loading big image immediatelly");
			}*/

			//img.setImageBitmap(thumb);


			BitmapAjaxCallback cb = new BitmapAjaxCallback() {

				//int failCount;


				@Override
				protected void callback(final String url, final ImageView iv, final Bitmap bm, final AjaxStatus status) {
					//super.callback(url, iv, bm, status);
					if (status.getCode() == 200) {
						//failCount = 0;
						iv.setImageBitmap(bm);
						L.v("retry count=" + getRetry());
						if (thumb != null) {
							L.v("thumb found");
						} else {
							L.w("no thumb. loading big image immediatelly");
							Animation anim = new AlphaAnimation(0, 1);
							anim.setInterpolator(new DecelerateInterpolator());
							anim.setDuration(500);
							iv.startAnimation(anim);

						}
					} else {
						L.e("error loading photo");
						/*failCount++;
						//this.retry()
						if (failCount < 5)
							loadImage(url, this);
						else
							failCount = 0;*/
					}
				}


				private int getRetry() {
					try {
						Field f = AbstractAjaxCallback.class.getDeclaredField("retry");
						f.setAccessible(true);
						return f.getInt(this);
					} catch (IllegalAccessException | NoSuchFieldException e) {
						e.printStackTrace();
						return -1;
					}
				}
			};
			cb.preset(thumb);
			cb.retry(5);

			loadImage(url, cb);

			return v;
		}


		private void loadImage(final String url, BitmapAjaxCallback cb) {
			//aq.progress(R.id.gallery_progress).image(url, true, true, mImageWidth, 0, thumb, fade);

			aq.progress(R.id.gallery_progress).image(url, true, true, mImageWidth, 0, cb);
		}


		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {

			mCurrentView = (View) object;

		}


		public ImageView getCurrentImg() {
			if (mCurrentView != null) {
				ImageView i = (ImageView) mCurrentView.findViewById(R.id.gallery_image);
				return i;
			}
			return null;
		}


		@Override
		public int getCount() {
			return numOfItems;
		}


		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}


		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			((ViewPager) container).removeView((View) object);
			// super.destroyItem(container,position,object);
		}

		/*
		 * void setOnSetPageListener(OnSetPageListener l) { listener = l; }
		 */

	}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	private class CommentsAdapter extends ArrayAdapter<Comment> {

		AQuery aq;
		Bitmap user;


		public CommentsAdapter(Context context, List<Comment> objects) {
			super(context, R.layout.row_comment, R.id.content, objects);
			aq = new AQuery(context);
			user = BitmapFactory.decodeResource(context.getResources(), R.drawable.anonymous);
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);
			Comment c = getItem(position);
			aq.recycle(v);
			aq.id(R.id.avatar).image(c.avatarUrl, true, true, 0, R.drawable.anonymous, null, AQuery.FADE_IN);
			aq.id(R.id.name).text(c.name);
			aq.id(R.id.content).text(c.content);
			aq.id(R.id.date).text(c.dateFormatted != null ? c.dateFormatted : c.dateRaw);
			if (c.status != null)
				aq.id(R.id.status).image(v.getResources().getIdentifier(c.status, "drawable", v.getContext().getPackageName())).visible();
			else
				aq.id(R.id.status).gone();

			if (c.rate.startsWith("0"))
				aq.id(R.id.rating).gone();
			else if (c.rate.startsWith("-"))
				aq.id(R.id.rating).text(c.rate).textColor(v.getResources().getColor(R.color.red)).visible();
			else
				aq.id(R.id.rating).text("+" + c.rate).textColor(v.getResources().getColor(R.color.green)).visible();

			return v;
		}

	}


}
