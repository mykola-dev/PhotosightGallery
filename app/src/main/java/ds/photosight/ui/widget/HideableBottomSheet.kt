package ds.photosight.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ds.photosight.R
import ds.photosight.utils.getWithReflection

class HideableBottomSheet<V : View>(context: Context, attrs: AttributeSet) : BottomSheetBehavior<V>(context, attrs) {

    private val hideBehavior = HideBottomViewOnScrollBehavior<V>(context, attrs)
    private var lastState: State = State.VISIBLE

    enum class State { VISIBLE, HIDDEN }

    var stateCallback: ((State) -> Unit)? = null

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
        // block when scrolling bottomsheet content
        if (target is RecyclerView && target.id == R.id.menuList) return

        hideBehavior.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)

        val state = getDockState()
        if (lastState != state) {
            lastState = state
            stateCallback?.invoke(state)
        }
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        hideBehavior.onLayoutChild(parent, child, layoutDirection)
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    fun getDockState(): State = when (hideBehavior.getWithReflection<Int>("currentState")) {
        1 -> State.HIDDEN
        2 -> State.VISIBLE
        else -> error("not supported")
    }
}