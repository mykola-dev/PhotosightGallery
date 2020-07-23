package ds.photosight.ui.adapter

import ds.photosight.R
import ds.photosight.model.asViewModel
import ds.photosight.parser.PhotoDetails
import ds.photosight.ui.widget.VotesWidget

class PhotoStatsAdapter(data: PhotoDetails) : SimpleAdapter<PhotoDetails>(R.layout.item_photo_stats, listOf(data)) {
    override fun onBind(holder: SimpleViewHolder, item: PhotoDetails, position: Int) {
        val votesView = holder.itemView as VotesWidget
        votesView.init(
            item.stats.asViewModel(),
            item.awards.map { votesView.resources.getIdentifier(it.toString(), "drawable", context.packageName) }
        )
        votesView.runAnimations()
    }

}