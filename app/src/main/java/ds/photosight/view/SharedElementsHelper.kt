package ds.photosight.view

import android.view.View
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import ds.photosight.R
import ds.photosight.view.adapter.SimpleViewHolder
import kotlinx.android.synthetic.main.item_viewer_photo.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SharedElementsHelper(private val fragment: Fragment) {

    private var elementPosition: Int = -1
    private var isRunning = true

    fun postpone(position: Int) {
        isRunning = true
        fragment.postponeEnterTransition(500, TimeUnit.MILLISECONDS)
        elementPosition = position
    }

    fun animate(position: Int) {
        if (elementPosition == position) {
            fragment.startPostponedEnterTransition()
            isRunning = false
        }
    }

    fun setupAnimation() = with(fragment) {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.image_transition)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.image_transition)
    }

    fun isAnimating(position: Int): Boolean = elementPosition == position && isRunning

    fun bindView(v: View, transitionName: String) {
        v.transitionName = transitionName
    }

    fun setupExitCallback(recyclerView: RecyclerView) {
        fragment.setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
                Timber.v("onMapSharedElements")
                Timber.v("names: $names")
                Timber.v("shared elements: ${sharedElements.toList().joinToString(", ", transform = { it.first + ":" + it.second.transitionName })}")

                // Locate the ViewHolder for the clicked position.
                val selectedViewHolder: SimpleViewHolder = recyclerView
                    .findViewHolderForAdapterPosition(elementPosition) as SimpleViewHolder? ?: return

                Timber.w("new shared item:${selectedViewHolder.photoImage.transitionName}")
                sharedElements[names[0]] = selectedViewHolder.photoImage
            }

            override fun onSharedElementEnd(sharedElementNames: MutableList<String>, sharedElements: MutableList<View>, sharedElementSnapshots: MutableList<View>?) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
                Timber.v("onSharedElementEnd")
            }

            override fun onSharedElementsArrived(sharedElementNames: MutableList<String>, sharedElements: MutableList<View>, listener: OnSharedElementsReadyListener) {
                super.onSharedElementsArrived(sharedElementNames, sharedElements, listener)
                Timber.v("onSharedElementsArrived")
            }

        })
        /* val transition: Transition = fragment.sharedElementEnterTransition as Transition
         transition.addListener(object : TransitionListenerAdapter() {
             override fun onTransitionEnd(transition: Transition) {
                 Timber.v("onTransitionEnd")
                 isRunning = false
             }
         })*/
    }

}