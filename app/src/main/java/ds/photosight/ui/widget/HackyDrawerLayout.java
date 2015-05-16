package ds.photosight.ui.widget;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import ds.photosight.utils.L;

public class HackyDrawerLayout extends DrawerLayout {


	public HackyDrawerLayout(Context context) {
		super(context);
	}


	public HackyDrawerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public HackyDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (Throwable t) {
			L.e("hacky drawer error");
			return false;
		}
	}
}