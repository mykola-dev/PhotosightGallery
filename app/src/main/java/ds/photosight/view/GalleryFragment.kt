package ds.photosight.view

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.R
import ds.photosight.model.MenuItemState
import ds.photosight.model.MenuState.Companion.MENU_CATEGORIES
import ds.photosight.model.MenuState.Companion.MENU_RATINGS
import ds.photosight.view.adapter.MenuAdapter
import ds.photosight.view.adapter.MenuPagerAdapter
import ds.photosight.viewmodel.MenuViewModel
import kotlinx.android.synthetic.main.view_menu.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class GalleryFragment : Fragment() {

    private val bottomSheet by lazy { BottomSheetBehavior.from(bottomSheetLayout) }

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

        val onMenuSelected = { item: MenuItemState ->
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            menuViewModel.onSelected(item)
        }
        val categoriesAdapter = MenuAdapter(onMenuSelected)
        val ratingsAdapter = MenuAdapter(onMenuSelected)
        menuViewPager.adapter = MenuPagerAdapter(categoriesAdapter, ratingsAdapter)
        setupTabs()

        // hide bottom sheet until categories loaded
        bottomSheet.isHideable = true
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN


        menuViewModel.menuStateLiveData.observe(viewLifecycleOwner) {
            log.v("menu state observed")

            categoriesAdapter.updateData(it.categories)
            ratingsAdapter.updateData(it.ratings)

            if (bottomSheet.state == BottomSheetBehavior.STATE_HIDDEN) {
                Handler().post {
                    bottomSheet.isHideable = false
                    bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

        }
    }

    private fun setupTabs() {
        TabLayoutMediator(tabLayout, menuViewPager) { tab, position ->
            tab.text = when (position) {
                MENU_CATEGORIES -> getString(R.string.categories)
                MENU_RATINGS -> getString(R.string.ratings)
                else -> error("wrong tab")
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayout.removeOnTabSelectedListener(tabsSelector)
    }

    private fun openMenu() {
        if (bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}
