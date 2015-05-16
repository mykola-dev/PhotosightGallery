package ds.photosight.ui.widget;


import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import ds.photosight.R;
import ds.photosight.utils.Utils;

import java.util.List;

@SuppressWarnings("ALL")
public class VotesWidget extends LinearLayout {

	private static final float ANIMATION_DURATION = 1500;
	private static int awardIconSize;// = Utils.dp(16);

	private List<Integer> mRates;
	private List<String> mAvards;
	private AQuery aq;
	private TextView[] mRateViews = new TextView[5];
	private View mGreenBar;
	private View mRedBar;


	public VotesWidget(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		aq = new AQuery(this);

	}


	public void setRates(final List<Integer> rates) {
		mRates = rates;
	}


	public void setAvards(final List<String> avards) {
		mAvards = avards;
	}


	public void runAnimations() {

		awardIconSize = getAwardIconSize(mAvards.size());
		mRateViews[0] = aq.id(R.id.aRate).getTextView();
		mRateViews[1] = aq.id(R.id.oRate).getTextView();
		mRateViews[2] = aq.id(R.id.tRate).getTextView();
		mRateViews[3] = aq.id(R.id.lRate).getTextView();
		mRateViews[4] = aq.id(R.id.dRate).getTextView();
		for (TextView t : mRateViews) { t.setText(""); }

		mGreenBar = aq.id(R.id.greenBar).getView();
		mRedBar = aq.id(R.id.redBar).getView();
		((ViewGroup) aq.id(R.id.labelsContainer).getView()).startLayoutAnimation();
		removeAvards();

		new AsyncTask<List<Integer>, Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(final List<Integer>[] params) {
				Interpolator interpolator = new AccelerateDecelerateInterpolator();
				List<Integer> values = params[0];
				float rating = 1 / (float) (values.get(3) + values.get(4)) * values.get(3);

				Integer[] results = new Integer[values.size() + 1];
				long start = System.currentTimeMillis();
				boolean escape = false;
				while (!escape) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					final float f = (System.currentTimeMillis() - start) / ANIMATION_DURATION;
					if (f >= 1)
						escape = true;

					for (int i = 0; i < values.size(); i++) {
						results[i] = (int) (values.get(i) * interpolator.getInterpolation(f));
					}

					//L.v("publish progress");
					results[results.length - 1] = (int) (rating * interpolator.getInterpolation(f) * 1000);
					publishProgress(results);

				}

				for (int i = 0; i < values.size(); i++) {
					results[i] = (int) (values.get(i));
				}
				publishProgress(results);

				return true;
			}


			@Override
			protected void onProgressUpdate(final Integer[] values) {
				for (int i = 0; i < values.length - 1; i++) {
					mRateViews[i].setText(String.valueOf(values[i]));
				}

				int height = values[values.length - 1] * mRedBar.getHeight() / 1000;
				//L.v("h=" + height);
				mGreenBar.getLayoutParams().height = height;
				//mGreenBar.setMinimumHeight();
			}


			@Override
			protected void onPostExecute(final Boolean o) {
				if (mAvards != null)
					addAvards();
			}
		}.execute(mRates);
	}


	private int getAwardIconSize(final int size) {
		if (size > 7)
			return Utils.dp(16);
		else if (size > 4)
			return Utils.dp(24);
		else
			return Utils.dp(32);
	}


	private void removeAvards() {
		((ViewGroup) aq.id(R.id.avardsContainer).getView()).removeAllViews();
	}


	private void addAvards() {
		final ViewGroup vg = (ViewGroup) aq.id(R.id.avardsContainer).getView();

		LinearLayout.LayoutParams lp = new LayoutParams(awardIconSize, awardIconSize);

		for (String a : mAvards) {
			final ImageView img = new ImageView(getContext());
			vg.addView(img);
			img.setLayoutParams(lp);

			final String url = a;
			img.post(new Runnable() {
				@Override
				public void run() {
					aq.id(img).image(url, true, true, 0, 0, null, R.anim.award_anim);

				}
			});
		}
	}


}
