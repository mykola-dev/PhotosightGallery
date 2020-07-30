package ds.photosight.ui.adapter

import android.text.format.DateUtils
import com.bumptech.glide.Glide
import ds.photosight.R
import ds.photosight.parser.PhotoDetails
import kotlinx.android.synthetic.main.item_comment.*

class CommentsAdapter(comments: List<PhotoDetails.Comment>) : SimpleAdapter<PhotoDetails.Comment>(R.layout.item_comment, comments) {

    override fun onBind(holder: SimpleViewHolder, item: PhotoDetails.Comment, position: Int) {
        holder.name.text = if (item.isAuthor) context.getString(R.string.photo_author)
        else item.author
        holder.content.text = item.text
        holder.date.text = DateUtils.getRelativeDateTimeString(context, item.timestamp, 0, DateUtils.DAY_IN_MILLIS, 0)
        holder.rating.text = if (item.likes > 0) "+${item.likes}" else ""
        Glide.with(holder.itemView)
            .load(item.avatar)
            .circleCrop()
            .transition(properTransition)
            .error(R.drawable.anonymous)
            .into(holder.avatar)
    }

}