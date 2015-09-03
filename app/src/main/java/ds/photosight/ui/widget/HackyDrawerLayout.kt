package ds.photosight.ui.widget

import android.content.Context
import android.support.v4.widget.DrawerLayout
import android.util.AttributeSet
import android.view.MotionEvent
import ds.photosight.utils.L

public class HackyDrawerLayout(context: Context, attrs: AttributeSet) : DrawerLayout(context,attrs) {


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (t: Throwable) {
            L.e("hacky drawer error")
            return false
        }

    }
}