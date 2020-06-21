package ds.photosight.view

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.androidquery.util.AQUtility.post
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.R
import ds.photosight.utils.observe
import ds.photosight.utils.postDelayed
import ds.photosight.view.MenuState.Companion.MENU_CATEGORIES
import ds.photosight.view.MenuState.Companion.MENU_RATINGS
import ds.photosight.view.adapter.SimpleAdapter
import ds.photosight.view.adapter.SimpleViewHolder
import ds.photosight.viewmodel.MenuViewModel
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.item_menu.*
import kotlinx.android.synthetic.main.page_menu.view.*
import kotlinx.android.synthetic.main.view_menu.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class GalleryFragment : Fragment() {

    @Inject
    lateinit var log: Timber.Tree

    private val tabsSelector = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
            openMenu()
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            openMenu()
        }

    }

    private val menuViewModel: MenuViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_gallery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout.addOnTabSelectedListener(tabsSelector)

        BottomSheetBehavior.from(bottomSheet).addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //nestedScroll.isNestedScrollingEnabled = newState == BottomSheetBehavior.STATE_COLLAPSED
            }
        })
/*        menuViewModel.categories.observe(viewLifecycleOwner) {
            log.v("categories: $it")
        }*/
        menuViewModel.categories.observe(viewLifecycleOwner) {
            log.v("categories: $it")
        }

        // hide bottom sheet until categories loaded
        BottomSheetBehavior.from(bottomSheet).apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuViewModel.menuState.observe(viewLifecycleOwner) {
            log.v("menu state observed")
            menuViewPager.adapter = MenuPagerAdapter(it) { item ->
                log.v("on selected: $item")
            }
            TabLayoutMediator(tabLayout, menuViewPager) { tab, position ->
                tab.text = when (position) {
                    MENU_CATEGORIES -> getString(R.string.categories)
                    MENU_RATINGS -> getString(R.string.ratings)
                    else -> error("wrong tab")
                }
            }.attach()

            Handler().post {
                BottomSheetBehavior.from(bottomSheet).apply {
                    isHideable = false
                    state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayout.removeOnTabSelectedListener(tabsSelector)
    }

    private fun openMenu() {
        val sheet = BottomSheetBehavior.from(bottomSheet)
        if (sheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
            sheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}

class MenuPagerAdapter(
    private val state: MenuState,
    private val onSelected: (MenuItemState) -> Unit
) : SimpleAdapter<Any>(R.layout.page_menu, (0..1).toList()) {

    override fun onBind(holder: SimpleViewHolder, item: Any, position: Int) {
        holder.itemView.menuList.adapter = when (position) {
            MENU_CATEGORIES -> MenuAdapter(state.categories, onSelected)
            MENU_RATINGS -> MenuAdapter(state.ratings, onSelected)
            else -> error("wrong tab")
        }
    }

}

class MenuAdapter(data: List<MenuItemState>, private val onSelected: (MenuItemState) -> Unit) : SimpleAdapter<MenuItemState>(R.layout.item_menu, data) {

    override fun onBind(holder: SimpleViewHolder, item: MenuItemState, position: Int) {
        holder.textLabel.text = item.title
        holder.itemView.setOnClickListener {
            onSelected(getItem(position))
        }
    }
}


data class MenuState(
    //val page: Int,
    //val item: Int,
    val categories: List<MenuItemState>,
    val ratings: List<MenuItemState>
) {
    companion object {
        const val MENU_CATEGORIES = 0
        const val MENU_RATINGS = 1
    }
}

data class MenuItemState(
    val menu: Int,
    val position: Int,
    val title: String,
    var isSelected: Boolean
)