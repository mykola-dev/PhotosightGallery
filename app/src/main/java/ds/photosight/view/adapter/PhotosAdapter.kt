package ds.photosight.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import ds.photosight.R
import ds.photosight.parser.PhotoInfo
import kotlinx.android.synthetic.main.item_photo.*
import kotlin.random.Random

class PhotosAdapter : RecyclerView.Adapter<SimpleViewHolder>() {

    var data: List<PhotoInfo> = emptyList()

    init {
        setHasStableIds(true)
    }

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long = data[position].id.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder =
        SimpleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false))


    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.photoImage.setBackgroundColor(randomColor())
        val item = data[position]
        Glide.with(holder.itemView)
            .load(item.thumb)
            .override(SIZE_ORIGINAL)
            .into(holder.photoImage)
    }

    private fun randomColor(): Int = Color.HSVToColor(floatArrayOf(Random.nextFloat() * 360, 1f, 1f))

    fun update(new: List<PhotoInfo>) {
        data = new
        notifyDataSetChanged()
    }

}