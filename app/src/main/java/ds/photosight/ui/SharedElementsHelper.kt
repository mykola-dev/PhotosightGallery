package ds.photosight.ui

import android.view.View
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import ds.photosight.R
import ds.photosight.utils.postDelayed
import timber.log.Timber
import java.util.concurrent.TimeUnit


class SharedElementsHelper(private val fragment: Fragment) {

    var isRunning = true
    private var elementPosition: Int = -1
    private var isScrollingToPosition = false

    fun postpone(position: Int) {
        isRunning = true
        fragment.postponeEnterTransition(2000, TimeUnit.MILLISECONDS)
        elementPosition = position
    }

    fun animate(position: Int) {
        fun doAnimate() {
            fragment.startPostponedEnterTransition()
            isRunning = false
        }
        if (elementPosition == position) {
            if (isScrollingToPosition) {
                postDelayed(500) {  // todo wait until grid is settled down
                    doAnimate()
                }
            } else {
                doAnimate()
            }
        }
    }

    fun setupEnterAnimation(onEnd: () -> Unit) = with(fragment) {
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.image_transition)
            .addListener(object : TransitionListenerAdapter() {
                override fun onTransitionEnd(transition: Transition) {
                    onEnd()
                }
            })
    }

    fun setupExitAnimation() = with(fragment) {
        exitTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.gallery_exit_transition)
    }

    fun isAnimating(position: Int): Boolean = elementPosition == position && isRunning

    fun bindView(v: View, transitionName: String) {
        v.transitionName = transitionName
    }

    fun setupEnterCallback(view: View) {
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
        if (!isRunning) {
            Timber.e("can't set callback when animation is stopped")
            return
        }
        fragment.setExitSharedElementCallback(TransitionCallback("exit", view) {
            fragment.setExitSharedElementCallback(null)
        })
    }

    fun moveToCurrentItem(recyclerView: RecyclerView) {
        if (isRunning && elementPosition >= 0) {
            val selectedViewHolder = recyclerView.findViewHolderForAdapterPosition(elementPosition)
            if (selectedViewHolder == null) {
                Timber.w("view not found for position $elementPosition. scrolling!")
                recyclerView.scrollToPosition(elementPosition)
                isScrollingToPosition = true
            }
        }
    }

    fun updatePosition(photosAdapter: RecyclerView.Adapter<*>) {
        if (elementPosition != -1) {
            if (isScrollingToPosition) {
                isScrollingToPosition = false
            }
            elementPosition = -1
        }
    }

    class TransitionCallback(val name: String, val view: View, val onFinish: () -> Unit) : SharedElementCallback() {
        override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
            sharedElements[names.first()] = view
            onFinish()
        }
    }
}