/*
package ds.photosight.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ds.photosight.R
import kotlinx.android.synthetic.main.page_menu.view.*
import timber.log.Timber

class FullscreenAppBarBehavior<V : View>(context: Context, attrs: AttributeSet) : BottomSheetBehavior<V>(context, attrs) {

    private val hideBehavior = HideBottomViewOnScrollBehavior<V>(context, attrs)

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        //Timber.v("onNestedScroll target=${target.javaClass.simpleName} dxConsumed=$dxConsumed dyConsumend=$dyConsumed dxUnconsumed=$dxUnconsumed dyUnconsumed=$dyUnconsumed type=$type consumed=${consumed.map { it }}")
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
        // block when scrolling bottomsheet content
        if (target is RecyclerView && target.id == R.id.menuList) return

        hideBehavior.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        hideBehavior.onLayoutChild(parent, child, layoutDirection)
        return super.onLayoutChild(parent, child, layoutDirection)
    }
}*/
