package ds.photosight.ui.adapter

import ds.photosight.R
import ds.photosight.ui.viewmodel.MenuItemState
import kotlinx.android.synthetic.main.item_menu.*

class MenuAdapter(private val onSelected: (MenuItemState) -> Unit) : SimpleAdapter<MenuItemState>(R.layout.item_menu, emptyList()) {

    override fun onBind(holder: SimpleViewHolder, item: MenuItemState, position: Int) {
        holder.textLabel.text = item.title
        holder.itemView.isActivated = item.isSelected
        holder.itemView.setOnClickListener {
            onSelected(getItem(position))
        }
    }

    override fun getItemId(position: Int): Long = getItem(position).title.hashCode().toLong()
}