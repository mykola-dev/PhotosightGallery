package ds.photosight.view.adapter

import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import ds.photosight.R
import ds.photosight.parser.PhotoInfo
import ds.photosight.view.SharedElementsHelper
import kotlinx.android.synthetic.main.item_gallery_photo.*

class GalleryAdapter(
    val transitionHelper: SharedElementsHelper,
    val onClick: (ClickedItem) -> Unit
) : PagingDataAdapter<PhotoInfo, PhotoViewHolder>(PhotoInfoDiffCallback) {

    data class ClickedItem(
        val view: ImageView,
        val position: Int,
        val isLoading: Boolean
    )

    private var clickedItem: Int? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(24)   // page size

        // fixes grid re-layout on each click
        recyclerView.itemAnimator = null

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val holder = PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_photo, parent, false))
        holder.itemView.setOnClickListener {
            val position = holder.absoluteAdapterPosition
            val item = getItem(position)!!
            if (!item.failed) {
                clickedItem = position
                onClick(ClickedItem(holder.photoImage, position, true))
            }
            notifyItemChanged(position)
        }
        return holder
    }


    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val item = getItem(position)!!
        val photoView = holder.photoImage
        transitionHelper.bindView(photoView, item.id.toString())

        val afterClick = clickedItem == position
        val settlingBack = transitionHelper.isAnimating(position)

        val url = if (afterClick || settlingBack) {
            item.large
        } else {
            item.thumb
        }
        Glide.with(photoView)
            .load(url)
            .run {
                if (!afterClick) {
                    placeholder(holder.placeholder).transition(properTransition)
                } else {
                    placeholder(photoView.drawable)
                }
            }
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .error(R.drawable.ic_photo_error)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    item.failed = true
                    return false
                }

                override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    item.failed = false
                    if (afterClick) {
                        clickedItem = null
                        onClick(ClickedItem(photoView, position, false))
                    }
                    if (settlingBack) {
                        transitionHelper.setupExitCallback(photoView)
                        transitionHelper.animate(position)

                    }

                    return false
                }
            })
            .into(photoView)

    }

}

class PhotoViewHolder(v: View) : SimpleViewHolder(v) {
    val placeholder = (ContextCompat.getDrawable(v.context, R.drawable.photo_placeholder) as AnimationDrawable).apply {
        setExitFadeDuration(1000)
        start()
    }
}

val properTransition = DrawableTransitionOptions.with { dataSource, isFirstResource ->
    if (dataSource == DataSource.REMOTE) DrawableCrossFadeFactory.Builder(500).build().build(dataSource, isFirstResource)
    else null
}