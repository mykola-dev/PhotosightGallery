package ds.photosight.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import ds.photosight.parser.PhotoInfo

object PhotoInfoDiffCallback : DiffUtil.ItemCallback<PhotoInfo>() {
    override fun areItemsTheSame(oldItem: PhotoInfo, newItem: PhotoInfo): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: PhotoInfo, newItem: PhotoInfo): Boolean = oldItem == newItem
}