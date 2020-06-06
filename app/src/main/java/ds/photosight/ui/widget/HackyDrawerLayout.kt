package ds.photosight.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.drawerlayout.widget.DrawerLayout
import ds.photosight.utils.L

class HackyDrawerLayout(context: Context, attrs: AttributeSet) : DrawerLayout(context,attrs) {


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean = try {
        super.onInterceptTouchEvent(ev)
    } catch (t: Throwable) {
        L.e("hacky drawer error")
        false
    }
}