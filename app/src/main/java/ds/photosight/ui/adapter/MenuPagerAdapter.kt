package ds.photosight.ui.adapter

import ds.photosight.R
import ds.photosight.ui.viewmodel.MenuState
import kotlinx.android.synthetic.main.page_menu.*
import timber.log.Timber

class MenuPagerAdapter(
    private val categoriesAdapter: MenuAdapter,
    private val ratingsAdapter: MenuAdapter
) : SimpleAdapter<Any>(R.layout.page_menu, (0..1).toList()) {

    override fun onBind(holder: SimpleViewHolder, item: Any, position: Int) {
        holder.menuList.adapter = when (position) {
            MenuState.MENU_CATEGORIES -> categoriesAdapter
            MenuState.MENU_RATINGS -> ratingsAdapter
            else -> error("wrong tab")
        }
    }

}