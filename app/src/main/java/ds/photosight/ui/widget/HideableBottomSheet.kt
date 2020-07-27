package ds.photosight.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ds.photosight.R
import ds.photosight.utils.recyclerView
import ds.photosight.utils.reflection
import kotlinx.android.synthetic.main.view_menu.*
import kotlinx.android.synthetic.main.view_menu.view.*
import timber.log.Timber
import java.lang.ref.WeakReference


class HideableBottomSheet<V : View>(context: Context, attrs: AttributeSet) : BottomSheetBehavior<V>(context, attrs) {

    private val hideBehavior = HideBottomViewOnScrollBehavior<V>(context, attrs)
    private var lastState: State = State.VISIBLE

    //private var nestedScrollingChildRef: WeakReference<View> by reflection()
    //private var viewRef: WeakReference<View> by reflection()
    private var currentState by hideBehavior.reflection<Int>()

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
        Timber.v("onNestedScroll child=${child.javaClass.simpleName} target=${target.javaClass.simpleName}")
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
        Timber.v("onLayoutChild ${child.javaClass.simpleName}")
        hideBehavior.onLayoutChild(parent, child, layoutDirection)
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    fun getDockState(): State = when (currentState) {
        1 -> State.HIDDEN
        2 -> State.VISIBLE
        else -> error("not supported")
    }

    // https://stackoverflow.com/questions/61465262/bottomsheetbehavior-with-viewpager2-cant-be-scrolled-down-by-nested-recyclervie
    fun fixScrollingBugs(viewPager: ViewPager2) {
        viewPager.offscreenPageLimit = viewPager.adapter?.itemCount ?: 0
        viewPager.recyclerView.isNestedScrollingEnabled = false
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                println("menu page $position selected")
                val currentView: RecyclerView = (viewPager.recyclerView.layoutManager as LinearLayoutManager).findViewByPosition(position) as RecyclerView
                viewPager
                    .recyclerView
                    .children
                    .filter { it is RecyclerView }
                    .toList()
                    .forEach { it.isNestedScrollingEnabled = it == currentView }

                // triggers bottomSheetBehavior.onLayoutChild
                viewPager.requestLayout()
            }
        })
    }
}