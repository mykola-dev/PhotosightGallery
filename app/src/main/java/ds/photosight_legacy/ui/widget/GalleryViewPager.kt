package ds.photosight_legacy.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

public class GalleryViewPager(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean = try {
        super.onInterceptTouchEvent(e)
    } catch (ex: IllegalArgumentException) {
        ex.printStackTrace()
        false
    }


    interface ClickListener {
        fun onClick()
    }

}