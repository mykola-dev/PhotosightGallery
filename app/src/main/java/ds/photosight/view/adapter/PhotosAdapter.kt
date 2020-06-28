package ds.photosight.view.adapter

import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.signature.ObjectKey
import ds.photosight.R
import ds.photosight.parser.PhotoInfo
import kotlinx.android.synthetic.main.item_photo.*
import timber.log.Timber
import kotlin.random.Random

class PhotosAdapter(val onClick: (PhotoInfo) -> Unit) : PagingDataAdapter<PhotoInfo, SimpleViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<PhotoInfo>() {
        override fun areItemsTheSame(oldItem: PhotoInfo, newItem: PhotoInfo): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PhotoInfo, newItem: PhotoInfo): Boolean = oldItem == newItem
    }

    private val glideVersion = Random.nextInt()

    private lateinit var placeholder: Drawable

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(24)
        placeholder = (ContextCompat.getDrawable(recyclerView.context, R.drawable.item_photo_placeholder) as AnimationDrawable).apply {
            setExitFadeDuration(1000)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val holder = SimpleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false))
        holder.itemView.setOnClickListener {
            onClick(getItem(holder.bindingAdapterPosition)!!)
        }
        return holder
    }


    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        Timber.d("onBindViewHolder $position")
        val item = getItem(position)!!
        val placeholder = (ContextCompat.getDrawable(holder.itemView.context, R.drawable.item_photo_placeholder) as AnimationDrawable).apply {
            setExitFadeDuration(1000)
            start()
        }
        Glide.with(holder.itemView)
            .load(item.thumb)
            //.load(placeholder)
            .placeholder(placeholder)
            //.override(SIZE_ORIGINAL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .dontTransform()
            .signature(ObjectKey(glideVersion))
            .into(holder.photoImage)

    }

}