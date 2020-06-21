package ds.photosight.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior

class HideableBottomSheet<V : View>(context: Context, attrs: AttributeSet) : BottomSheetBehavior<V>(context, attrs) {

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
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
        //println("onNestedScroll target=${target.javaClass.simpleName} dxConsumed=$dxConsumed dyConsumend=$dyConsumed dxUnconsumed=$dxUnconsumed dyUnconsumed=$dyUnconsumed type=$type consumed=${consumed.map { it }}")
        if (target is NestedScrollView) {   // block when scrolling bottomsheet content
            hideBehavior.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
        }
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        hideBehavior.onLayoutChild(parent, child, layoutDirection)
        return super.onLayoutChild(parent, child, layoutDirection)
    }
}