package ds.photosight.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.progressindicator.ProgressIndicator
import ds.photosight.R
import ds.photosight.parser.PhotoInfo
import kotlinx.android.synthetic.main.item_photo.*
import timber.log.Timber
import kotlin.random.Random

class PhotosAdapter : PagingDataAdapter<PhotoInfo, SimpleViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<PhotoInfo>() {
        override fun areItemsTheSame(oldItem: PhotoInfo, newItem: PhotoInfo): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PhotoInfo, newItem: PhotoInfo): Boolean = oldItem == newItem
    }

    private val glideVersion = Random.nextInt()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(24)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder =
        SimpleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false))

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        Timber.d("onBindViewHolder $position")
        val item = getItem(position)!!

        //val progress= CircularIndeterminateDrawable()

        Glide.with(holder.itemView)
            .load(item.thumb)

            .placeholder(R.color.background)
            //.override(SIZE_ORIGINAL)
            .dontTransform()
            .signature(ObjectKey(glideVersion))
            .into(holder.photoImage)

    }

    //private fun randomColor(): Int = Color.HSVToColor(floatArrayOf(Random.nextFloat() * 360, 1f, 1f))

}