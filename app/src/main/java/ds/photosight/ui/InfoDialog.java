package ds.photosight.ui;

import java.io.Serializable;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import ds.photosight.Constants;
import ds.photosight.R;
import ds.photosight.ui.widget.Histogram;
import ds.photosight.utils.L;

public class InfoDialog extends DialogFragment implements Constants {

	Map<Integer, String> data;


	public InfoDialog(Map<Integer, String> map) {
		super();
		data = map;
	}


	public InfoDialog() {
		super();
	}


	@Override
	public void onCreate(Bundle b) {
		setStyle(STYLE_NORMAL, android.R.style.Theme_Panel);

		//getDialog().setOnCancelListener(this);
		if (b != null) {
			data = (Map<Integer, String>) b.get("map");
		}
		super.onCreate(b);
	}


	@Override
	public void onSaveInstanceState(Bundle b) {
		b.putSerializable("map", (Serializable) data);
		super.onSaveInstanceState(b);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
		lp.dimAmount = 0.4f;
		getDialog().getWindow().setAttributes(lp);
		getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		final View v = inflater.inflate(R.layout.info, null);

		return v;
	}


	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		return new AnimatedDialog(getActivity(), getTheme());
	}


	@Override
	public void onViewCreated(final View v, final Bundle savedInstanceState) {
		super.onViewCreated(v, savedInstanceState);

		final LinearLayout table = (LinearLayout) v.findViewById(R.id.table);
		TextView title = (TextView) v.findViewById(R.id.titleText);
		title.setText(data.get(DATA_IMG_NAME));

		// fetch bitmap
		fetchImage(data);

		table.setVisibility(View.INVISIBLE);
		Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_in);
		v.startAnimation(a);
		a.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) { }


			@Override
			public void onAnimationRepeat(Animation animation) { }


			@Override
			public void onAnimationEnd(Animation animation) {
				table.setVisibility(View.VISIBLE);
				Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
				table.startAnimation(a);
			}
		});
	}


	private void fetchImage(final Map<Integer, String> data) {

		final AQuery aq = new AQuery(getView());
		Bitmap b = aq.getCachedImage(data.get(DATA_URL_LARGE));
		if (b == null)
			b = aq.getCachedImage(data.get(DATA_URL_SMALL));
		if (b != null)
			((Histogram) aq.id(R.id.histogram).getView()).setBitmap(b);
		else {
			AQUtility.time("fetch");
			aq.ajax(data.get(DATA_URL_LARGE), Bitmap.class, new AjaxCallback<Bitmap>() {
				@Override
				public void callback(final String url, final Bitmap b, final AjaxStatus status) {
					super.callback(url, b, status);
					if (b != null) {
						L.v("bitmap fetched. width=" + b.getWidth());
						AQUtility.timeEnd("fetch", 0);
						((Histogram) aq.id(R.id.histogram).getView()).setBitmap(b);
					}
				}
			});
		}
	}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	private static class AnimatedDialog extends Dialog {

		public AnimatedDialog(final Context context, final int theme) {
			super(context, theme);

		}


		@Override
		public boolean onTouchEvent(final MotionEvent event) {

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				finalAnimate();
				return true;
			}

			return false;

		}


		@Override
		public void onBackPressed() {
			finalAnimate();
		}


		public void finalAnimate() {

			final View v = findViewById(R.id.infoLayuot);
			final LinearLayout table = (LinearLayout) v.findViewById(R.id.table);

			Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
			table.startAnimation(a);
			a.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}


				@Override
				public void onAnimationRepeat(Animation animation) {
				}


				@Override
				public void onAnimationEnd(Animation animation) {
					table.setVisibility(View.INVISIBLE);
					Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.anim_out);
					v.startAnimation(a);
					a.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) { }


						@Override
						public void onAnimationRepeat(Animation animation) { }


						@Override
						public void onAnimationEnd(Animation animation) {
							v.post(new Runnable() {
								@Override
								public void run() {
									dismiss();
								}
							});
						}
					});
				}
			});
		}

	}


}

