package ds.photosight.ui.view

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.R
import ds.photosight.core.shareImage
import ds.photosight.core.shareUrl
import ds.photosight.parser.PhotoDetails
import ds.photosight.parser.PhotoInfo
import ds.photosight.ui.adapter.CommentsAdapter
import ds.photosight.ui.adapter.PhotoStatsAdapter
import ds.photosight.ui.adapter.ViewerAdapter
import ds.photosight.ui.viewmodel.CommentsState
import ds.photosight.ui.viewmodel.MainViewModel
import ds.photosight.ui.viewmodel.ViewerViewModel
import ds.photosight.utils.*
import kotlinx.android.synthetic.main.fragment_viewer.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ViewerFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: ViewerViewModel by viewModels()

    //private val args by navArgs<ViewerFragmentArgs>()
    private val transitionHelper = SharedElementsHelper(this)

    @Inject
    lateinit var log: Timber.Tree

    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transitionHelper.setupAnimation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_viewer, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.navigationBarColor = Color.TRANSPARENT
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        log.v("viewer created")
        transitionHelper.postpone(viewModel.position)

        setupDrawer()
        setupInsets()

        toggleUiElements(false)

        setupViewPager()

        menuSetup()

        fab.setOnClickListener {
            toggleShareMenu()
        }
    }

    private fun menuSetup() {
        bottomToolbar.setOnMenuItemClickListener {
            when (it.itemId) {

            }
            requireActivity().toast("todo")

            true
        }
        shareMenuView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.im_share_link -> requireContext().shareUrl(getPhotoItem().pageUrl)
                R.id.im_share_img -> requireContext().shareImage(getPhotoItem().large)
            }
            toggleShareMenu()
            true
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(shareMenuView) { v, insets ->
            v.setPadding(0, 0, 0, 0)    // buggy NavigationView fix
            insets
        }
    }

    private fun setupViewPager() {
        val adapter = ViewerAdapter(transitionHelper) {
            log.v("on clicked")
            toggleUiElements(!isActionBarVisible())
        }
        viewPager.adapter = adapter
        var shouldSettle = true
        mainViewModel.photosPagedLiveData.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
            if (shouldSettle) {
                shouldSettle = false
                viewPager.setCurrentItem(viewModel.position, false)
            }
        }

        viewPager.offscreenPageLimit = 1

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                log.v("on page selected: $position")
                findNavController().previousBackStackEntry?.savedStateHandle?.position = position
                viewModel.savedStateHandle.position = position
                val item = adapter.getItemAt(position)
                toolbar.title = item.title
                toolbar.subtitle = item.authorName
                val photoView = viewPager.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<View>(R.id.photoImage) ?: return
                transitionHelper.setupEnterCallback(photoView)
                if (shareMenu.isVisible) toggleShareMenu()
            }
        })
    }

    private fun isActionBarVisible(): Boolean = toolbar.isVisible

    private fun setupDrawer() {
        //(activity as AppCompatActivity).setSupportActionBar(bottomToolbar)
        drawerToggle = object : ActionBarDrawerToggle(requireActivity(), drawerLayout, bottomToolbar, 0, 0) {

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                val photoId = getPhotoItem().id
                viewModel.loadComments(photoId)
                log.v("loading comments for position=${viewModel.position} id=$photoId")
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                viewModel.onDrawerClosed()
            }
        }
        //drawerToggle.isDrawerIndicatorEnabled = false
        //bottomToolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_action_menu)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        viewModel.commentsState.observe(viewLifecycleOwner) { state ->
            drawerProgress.isVisible = state.loading
            errorView.isVisible = state.error
            commentsList.isVisible = state is CommentsState.Payload

            if (state is CommentsState.Payload) {
                log.v("data: ${state.details}")
                val data = state.details
                val infoAdapter = PhotoStatsAdapter(data)
                val commentsAdapter = CommentsAdapter(data.comments)
                commentsList.adapter = ConcatAdapter(infoAdapter, commentsAdapter)
            }
        }
    }

    private fun getPhotoItem(): PhotoInfo = (viewPager.adapter as ViewerAdapter).getItemAt(viewModel.position)

    private fun toggleUiElements(show: Boolean) {
        when {
            show -> {
                toolbar.isVisible = true
                bottomToolbar.isVisible = true
                bottomToolbar.performShow()
                fab.show()
            }
            shareMenu.isVisible -> {
                toggleShareMenu()
            }
            else -> {
                toolbar.isVisible = false
                bottomToolbar.performHide()
                fab.hide()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
        Timber.v("onConfigurationChanged")
    }

    private fun toggleShareMenu() {
        val views = listOf<View>(fab, shareMenu).sortedBy { !it.isVisible }

        val shareMenuTransform = MaterialContainerTransform().apply {
            startView = views.first()
            endView = views.last()
            addTarget(views.last())
            setPathMotion(MaterialArcMotion())
            scrimColor = Color.TRANSPARENT
            duration = 200
            //fadeProgressThresholds = MaterialContainerTransform.ProgressThresholds(0f, 1f)
            //scaleProgressThresholds = MaterialContainerTransform.ProgressThresholds(0f, 1f)
            setAllContainerColors(ContextCompat.getColor(requireContext(), R.color.accent))
            isElevationShadowEnabled = false
        }.addListener(object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                // https://github.com/material-components/material-components-android/issues/1304
                views.first().isInvisible = true
            }
        })
        views.last().isVisible = true
        TransitionManager.beginDelayedTransition(root, shareMenuTransform)

    }
}

