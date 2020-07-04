package ds.photosight.view

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.R
import ds.photosight.utils.toggle
import ds.photosight.view.adapter.MenuAdapter
import ds.photosight.view.adapter.MenuPagerAdapter
import ds.photosight.view.adapter.GalleryAdapter
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

    private lateinit var bottomSheet: BottomSheetBehavior<*>

    @Inject
    lateinit var log: Timber.Tree

    private val galleryViewModel: GalleryViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val transitionHelper = SharedElementsHelper(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_gallery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.primary)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary)


        setupMenu()

        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.get<Int>("position")
            ?.let { position ->
                log.v("observed new position: $position")
                transitionHelper.postpone(position)
            }

        bottomSheet = BottomSheetBehavior.from(bottomSheetLayout)
        fixInsets()

        val onMenuSelected = { item: MenuItemState ->
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            galleryViewModel.onMenuSelected(item)
        }
        val categoriesAdapter = MenuAdapter(onMenuSelected)
        val ratingsAdapter = MenuAdapter(onMenuSelected)
        menuViewPager.adapter = MenuPagerAdapter(categoriesAdapter, ratingsAdapter)
        setupTabs()

        // hide bottom sheet until categories loaded
        bottomSheet.isHideable = true
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        val photosAdapter = GalleryAdapter(transitionHelper) { clickedItem ->
            log.v("clicked on ${clickedItem.view.transitionName} pos=${clickedItem.position}")
            if (clickedItem.isLoading) {
                galleryViewModel.loadingState.value = true
            } else {
                val extras = FragmentNavigatorExtras(
                    clickedItem.view to clickedItem.view.transitionName
                )
                findNavController().navigate(GalleryFragmentDirections.openViewer(clickedItem.position), extras)
            }
        }

        photosRecyclerView.adapter = photosAdapter

        mainViewModel.setMenuStateLiveData(galleryViewModel.menuStateLiveData)

        galleryViewModel.menuStateLiveData.observe(viewLifecycleOwner) {
            log.v("menu state observed")
            categoriesAdapter.updateData(it.categories)
            ratingsAdapter.updateData(it.ratings)

            if (bottomSheet.isHideable) {
                Handler().post {
                    bottomSheet.isHideable = false
                    bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
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

    private fun fixInsets() {
        //bottomSheet.setb
        ViewCompat.setOnApplyWindowInsetsListener(bottomSheetLayout) { view, insets ->
            val initialPeekHeight = resources.getDimension(R.dimen.peek_height).toInt()
            log.e("bottom inset=${insets.systemWindowInsetBottom} top=${insets.systemWindowInsetTop}")
            val lp = bottomSheetLayout.layoutParams as CoordinatorLayout.LayoutParams
            lp.topMargin = insets.systemWindowInsetTop
            bottomSheetLayout.layoutParams = lp
            val bottomInset = insets.systemWindowInsetBottom + insets.systemWindowInsetTop
            bottomSheet.setPeekHeight(initialPeekHeight + bottomInset, true)
            insets
        }
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
        if (bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun Activity.showAbout() {
        val version: String = try {
            val manager = packageManager.getPackageInfo(packageName, 0)
            manager.versionName
        } catch (e1: PackageManager.NameNotFoundException) {
            "100500"
        }

        val message = getString(R.string.abouttext, getString(R.string.changelog))

        val d = MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.about_title_) + version)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.cancel()
            }
            .create()
        d.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        d.show()

    }
}


