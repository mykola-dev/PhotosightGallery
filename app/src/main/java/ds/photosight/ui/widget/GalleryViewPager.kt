package ds.photosight.ui.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

public class GalleryViewPager(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {

/*    public var zoomMode: Boolean = false
    public var needInit: Boolean = true

    private val x: Int = 0
    private val y: Int = 0
    private val mClickListener: ClickListener? = null*/


    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        try {
            return super.onInterceptTouchEvent(e)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
            return false
        }
    }


    public trait ClickListener {
        public fun onClick()
    }

}