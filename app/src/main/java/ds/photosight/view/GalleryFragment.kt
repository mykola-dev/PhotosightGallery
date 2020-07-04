package ds.photosight.view

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.R
import ds.photosight.utils.toggle
import ds.photosight.view.adapter.GalleryAdapter
import ds.photosight.view.adapter.MenuAdapter
import ds.photosight.view.adapter.MenuPagerAdapter
import ds.photosight.viewmodel.GalleryViewModel
import ds.photosight.viewmodel.MainViewModel
import ds.photosight.viewmodel.MenuItemState
import ds.photosight.viewmodel.MenuState.Companion.MENU_CATEGORIES
import ds.photosight.viewmodel.MenuState.Companion.MENU_RATINGS
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.view_menu.*
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class GalleryFragment : Fragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    @Inject
    lateinit var log: Timber.Tree

    private val galleryViewModel: GalleryViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val transitionHelper = SharedElementsHelper(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_gallery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.translucent)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.translucent)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)

        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.get<Int>("position")
            ?.let { position ->
                log.v("observed new position: $position")
                transitionHelper.postpone(position)
            }

        setupMenu()
        setupAppBar()
        fixInsets()
        observeData()

    }

    private fun observeData() {
        val onMenuSelected = { item: MenuItemState ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            galleryViewModel.onMenuSelected(item)
        }
        val categoriesAdapter = MenuAdapter(onMenuSelected)
        val ratingsAdapter = MenuAdapter(onMenuSelected)
        menuViewPager.adapter = MenuPagerAdapter(categoriesAdapter, ratingsAdapter)
        setupTabs()

        // hide bottom sheet until categories loaded
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val photosAdapter = GalleryAdapter(transitionHelper) { clickedItem ->
            log.v("clicked on ${clickedItem.view.transitionName} pos=${clickedItem.position}")
            if (clickedItem.isLoading) {
                galleryViewModel.loadingState.value = true
            } else {
                val extras = FragmentNavigatorExtras(
                    clickedItem.view to clickedItem.view.transitionName
                )
                with(findNavController()) {
                    log.v("current destination: ${currentDestination?.id}")
                    if (R.id.galleryFragment == currentDestination?.id) {
                        navigate(GalleryFragmentDirections.openViewer(clickedItem.position), extras)
                    } else {
                        log.w("wrong destination")
                    }
                }
            }
        }

        photosRecyclerView.adapter = photosAdapter

        mainViewModel.setMenuStateLiveData(galleryViewModel.menuStateLiveData)

        galleryViewModel.menuStateLiveData.observe(viewLifecycleOwner) {
            log.v("menu state observed")
            categoriesAdapter.updateData(it.categories)
            ratingsAdapter.updateData(it.ratings)

            if (bottomSheetBehavior.isHideable) {
                Handler().post {
                    bottomSheetBehavior.isHideable = false
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                photosAdapter.addLoadStateListener { state ->
                    galleryViewModel.loadingState.value = state.refresh is LoadState.Loading || state.append is LoadState.Loading || state.prepend is LoadState.Loading
                }
            }

        }

        mainViewModel.photosPagedLiveData.observe(viewLifecycleOwner) { photos ->
            log.v("photos list observed")
            photosAdapter.submitData(lifecycle, photos)
            transitionHelper.moveToCurrentItem(photosRecyclerView)
        }
        galleryViewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            log.v("isloading observed")
            progress.toggle(isLoading)
        }
    }

    private fun setupAppBar() {
        // fixes returning image transition glitch
        appBar.setExpanded(true, false)

        val topPadding = toolbar.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val sbInset = insets.systemWindowInsetTop
            view.updatePadding(top = topPadding + sbInset)
            insets
        }
    }

    private fun fixInsets() {
        val initialPeekHeight = resources.getDimension(R.dimen.peek_height).toInt()
        var sbInset = 0
        var nbInset = 0

        ViewCompat.setOnApplyWindowInsetsListener(bottomSheetLayout) { view, insets ->
            sbInset = insets.systemWindowInsetTop
            nbInset = insets.systemWindowInsetBottom
            bottomSheetBehavior.setPeekHeight(initialPeekHeight + nbInset, true)
            tabLayout.updatePadding(
                top = 0,
                bottom = nbInset
            )
            insets
        }
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //log.v("on slide")
                tabLayout.updatePadding(
                    top = (sbInset * slideOffset).toInt(),
                    bottom = (nbInset * (1 - slideOffset)).toInt()
                )
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })
    }

    private fun setupMenu() {
        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.im_about -> requireActivity().showAbout()
                else -> error("not implemented")
            }
            true
        }
    }

    private fun setupTabs() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                openMenu()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                openMenu()
            }

        })

        TabLayoutMediator(tabLayout, menuViewPager) { tab, position ->
            tab.text = when (position) {
                MENU_CATEGORIES -> getString(R.string.categories)
                MENU_RATINGS -> getString(R.string.ratings)
                else -> error("wrong tab")
            }
        }.attach()
    }

    private fun openMenu() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

}


