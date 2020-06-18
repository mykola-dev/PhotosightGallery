package ds.photosight.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.ui.unit.dp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.sevensevenlabs.multidelivery.util.invoke
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.R
import ds.photosight.viewmodel.MenuViewModel
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.fragment_gallery.view.*
import kotlinx.android.synthetic.main.view_menu.*

@AndroidEntryPoint
class GalleryFragment : Fragment() {

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

        menuViewModel.showSnackbarCommand("hello")
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