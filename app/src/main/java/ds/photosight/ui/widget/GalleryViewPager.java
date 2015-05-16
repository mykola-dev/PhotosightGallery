package ds.photosight.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class GalleryViewPager extends ViewPager {

	public boolean zoomMode = false, needInit = true;

	private int x, y;
	private ClickListener mClickListener;


	public GalleryViewPager(Context context) {
		this(context, null);

	}


	public GalleryViewPager(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}


/*	public void setOnClickListener(ClickListener l) {
		mClickListener = l;
	}*/


	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {

		try {
			return super.onInterceptTouchEvent(e);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			return false;
		}
	}



	public interface ClickListener {

		public void onClick();
	}

}