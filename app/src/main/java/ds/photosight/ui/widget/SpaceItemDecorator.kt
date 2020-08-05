package ds.photosight.ui.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ds.photosight.utils.dp


class SpaceItemDecorator(private val dp: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val spacing = dp.dp
        outRect.set(spacing, spacing, spacing, spacing)
    }
}
