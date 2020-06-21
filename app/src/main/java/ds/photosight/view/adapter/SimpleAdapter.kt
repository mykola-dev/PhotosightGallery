package ds.photosight.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import java.lang.reflect.ParameterizedType

abstract class SimpleAdapter<D : Any>(
    private val layoutId: Int,
    var data: List<D> = emptyList()
) : RecyclerView.Adapter<SimpleViewHolder>() {

    protected lateinit var context: Context

    override fun getItemCount(): Int = data.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        context = recyclerView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)
        return SimpleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val item = getItem(position)
        onBind(holder, item, position)
    }

    fun getItem(position: Int): D = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    abstract fun onBind(holder: SimpleViewHolder, item: D, position: Int)
}

class SimpleViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer