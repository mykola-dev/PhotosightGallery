package ds.photosight.ui.adapter

import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import ds.photosight.R
import ds.photosight.parser.PhotoInfo
import ds.photosight.repo.PAGE_SIZE
import ds.photosight.ui.SharedElementsHelper
import kotlinx.android.synthetic.main.item_gallery_photo.*
import timber.log.Timber


class GalleryAdapter(
    private val transitionHelper: SharedElementsHelper,
    val onClick: (ClickedItem) -> Unit
) : PagingDataAdapter<PhotoInfo, PhotoViewHolder>(PhotoInfoDiffCallback) {

    data class ClickedItem(
        val view: ImageView,
        val position: Int,
        val isLoading: Boolean
    )

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: StaggeredGridLayoutManager

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(PAGE_SIZE)

        layoutManager = (recyclerView.layoutManager as StaggeredGridLayoutManager)

        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        //recyclerView.itemAnimator = null
        //layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val holder = PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_photo, parent, false))
        holder.itemView.setOnClickListener {
            val position = holder.absoluteAdapterPosition
            val item = getItem(position)!!
            onClick(ClickedItem(holder.photoImage, position, true))
            //notifyItemChanged(position)
            holder.loadImage(item.large, true, item, position) {
                onClick(ClickedItem(holder.photoImage, position, false))
            }

        }
        return holder
    }


    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        Timber.d("onBindViewHolder: $position")
        val item = getItem(position)!!
        val photoView = holder.photoImage
        transitionHelper.bindView(photoView, item.id.toString())

        val url = if (transitionHelper.isAnimating(position)) {
            item.large
        } else {
            item.thumb
        }

        holder.loadImage(url, false, item, position) {
            if (transitionHelper.isAnimating(position)) {
                transitionHelper.setupExitCallback(photoView)
                transitionHelper.animate(position)
            }
        }

    }

    private fun PhotoViewHolder.loadImage(url: String, afterClick: Boolean, item: PhotoInfo, position: Int, callback: () -> Unit) {
        Glide
            .with(this.itemView)
            .load(url)
            .run {
                if (!afterClick) {
                    placeholder(placeholder).transition(properTransition)
                } else {
                    placeholder(photoImage.drawable)
                }
            }
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .error(R.drawable.ic_photo_error)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    callback()
                    return false
                }
            })
            .into(photoImage)
    }

    fun getItemAt(position: Int): PhotoInfo = getItem(position) ?: error("wrong position")

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