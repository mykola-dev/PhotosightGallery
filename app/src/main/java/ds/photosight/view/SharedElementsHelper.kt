package ds.photosight.view

import android.view.View
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import ds.photosight.R
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SharedElementsHelper(private val fragment: Fragment) {

    private var elementPosition: Int = -1
    private var isRunning = true

    fun postpone(position: Int) {
        Timber.v("${fragment.javaClass.simpleName}:postpone animation for element $position")
        isRunning = true
        fragment.postponeEnterTransition(500, TimeUnit.MILLISECONDS)
        elementPosition = position
    }

    fun animate(position: Int) {
        Timber.d("${fragment.javaClass.simpleName}: animate $position")
        if (elementPosition == position) {
            Timber.v("start new animation at position $position")
            fragment.startPostponedEnterTransition()
            isRunning = false
            elementPosition = -1
        }
    }

    fun setupAnimation() = with(fragment) {
        Timber.d("${fragment.javaClass.simpleName}:setup animations")
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.image_transition)
    }

    fun isAnimating(position: Int): Boolean = elementPosition == position && isRunning

    fun bindView(v: View, transitionName: String) {
        v.transitionName = transitionName
    }

    fun setupEnterCallback(view: View) {
        Timber.v("${fragment.javaClass.simpleName}: setting new enter callback")
        if (isRunning) {
            Timber.e("can't set callback when animation is running")
            return
        }
        fragment.setEnterSharedElementCallback(TransitionCallback("enter", view) {
            fragment.setEnterSharedElementCallback(null)
        })
    }

    fun setupExitCallback(view: View) {
        Timber.v("${fragment.javaClass.simpleName}: setting new exit callback")
        fragment.setExitSharedElementCallback(TransitionCallback("exit", view) {
            fragment.setExitSharedElementCallback(null)
        })

    }

    fun moveToCurrentItem(recyclerView: RecyclerView) {
        if (isRunning && elementPosition >= 0) {
            Timber.v("${fragment.javaClass.simpleName}: scroll grid to updated position")
            val selectedViewHolder = recyclerView.findViewHolderForAdapterPosition(elementPosition)
            if (selectedViewHolder == null) recyclerView.scrollToPosition(elementPosition)
        }
    }

    class TransitionCallback(val name: String, val view: View, val onFinish: () -> Unit) : SharedElementCallback() {
        override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
            sharedElements[names.first()] = view
            onFinish()
        }
    }
}