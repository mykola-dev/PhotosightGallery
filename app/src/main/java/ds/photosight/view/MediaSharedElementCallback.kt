package ds.photosight.view

import android.view.View
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import java.util.*

class MediaSharedElementCallback : SharedElementCallback() {

    private val sharedElementViews: MutableList<View>

    fun setSharedElementViews(vararg sharedElementViews: View) {
        this.sharedElementViews.clear()
        this.sharedElementViews.addAll(listOf(*sharedElementViews))
    }

    override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
        if (sharedElementViews.isNotEmpty()) {
            removeObsoleteElements(names, sharedElements, mapObsoleteElements(names))
            for (sharedElementView in sharedElementViews) {
                val transitionName = ViewCompat.getTransitionName(sharedElementView)
                names.add(transitionName!!)
                sharedElements[transitionName] = sharedElementView
            }
        }
    }

    override fun onSharedElementEnd(
        sharedElementNames: List<String>,
        sharedElements: List<View>,
        sharedElementSnapshots: List<View>
    ) {
        for (sharedElementView in sharedElementViews) {
            forceSharedElementLayout(sharedElementView)
        }
    }

    /**
     * Maps all views that don't start with "android" namespace.
     *
     * @param names All shared element names.
     * @return The obsolete shared element names.
     */
    private fun mapObsoleteElements(names: List<String>): List<String> {
        val elementsToRemove: MutableList<String> = ArrayList(names.size)
        for (name in names) {
            if (name.startsWith("android")) continue
            elementsToRemove.add(name)
        }
        return elementsToRemove
    }

    /**
     * Removes obsolete elements from names and shared elements.
     *
     * @param names            Shared element names.
     * @param sharedElements   Shared elements.
     * @param elementsToRemove The elements that should be removed.
     */
    private fun removeObsoleteElements(
        names: MutableList<String>,
        sharedElements: MutableMap<String, View>,
        elementsToRemove: List<String>
    ) {
        if (elementsToRemove.isNotEmpty()) {
            names.removeAll(elementsToRemove)
            for (elementToRemove in elementsToRemove) {
                sharedElements.remove(elementToRemove)
            }
        }
    }

    private fun forceSharedElementLayout(view: View) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(view.width,
            View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(view.height,
            View.MeasureSpec.EXACTLY)
        view.measure(widthSpec, heightSpec)
        view.layout(view.left, view.top, view.right, view.bottom)
    }

    init {
        sharedElementViews = ArrayList()
    }
}