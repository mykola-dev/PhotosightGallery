package ds.photosight.ui.widget

import android.content.Context
import android.support.v4.widget.DrawerLayout
import android.util.AttributeSet
import android.view.MotionEvent
import ds.photosight.utils.L

public class HackyDrawerLayout(context: Context, attrs: AttributeSet) : DrawerLayout(context,attrs) {


  /*  public constructor(context: Context) : super(context) {
    }


    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }


    public constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
    }*/


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (t: Throwable) {
            L.e("hacky drawer error")
            return false
        }

    }
}