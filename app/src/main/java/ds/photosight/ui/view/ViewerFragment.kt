package ds.photosight.ui.view

import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.R
import ds.photosight.core.*
import ds.photosight.parser.PhotoInfo
import ds.photosight.ui.SharedElementsHelper
import ds.photosight.ui.adapter.CommentsAdapter
import ds.photosight.ui.adapter.PhotoStatsAdapter
import ds.photosight.ui.adapter.ViewerAdapter
import ds.photosight.ui.viewmodel.CommentsState
import ds.photosight.ui.viewmodel.MainViewModel
import ds.photosight.ui.viewmodel.ViewerViewModel
import ds.photosight.utils.position
import ds.photosight.utils.recyclerView
import ds.photosight.utils.snack
import kotlinx.android.synthetic.main.fragment_viewer.*
import kotlinx.coroutines.launch
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

    @Suppress("BlockingMethodInNonBlockingContext")
    val saveFileRequest = registerForActivityResult(SaveImage()) { uri: Uri? ->
        if (uri != null) {
            log.v("on result received $uri")
            lifecycleScope.launch {
                val file = requireContext().loadGlideFile(getPhotoItem().large)
                requireContext().contentResolver.openOutputStream(uri)?.use {
                    it.write(file.inputStream().readBytes())
                }
                showSnack(getString(R.string.saved_successfully))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transitionHelper.postpone(viewModel.position)
        transitionHelper.setupEnterAnimation {
            mainViewModel.onTransitionEnd()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_viewer, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.navigationBarColor = Color.TRANSPARENT
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        log.v("viewer created")

        setupDrawer()

        setupInsets()

        toggleUiElements(false)

        setupViewPager()

        setupMenu()

        fab.setOnClickListener {
            toggleShareMenu()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            when {
                drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.close()
                else -> findNavController().popBackStack()
            }
        }
    }

    private fun setupMenu() {
        bottomToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.im_info -> {
                    val action = ViewerFragmentDirections.actionViewerFragmentToDetailsFragment(getPhotoItem())
                    findNavController().navigate(action)
                }
                R.id.im_open_in_browser -> requireContext().openInBrowser(getPhotoItem().pageUrl)
                R.id.im_save -> savePhoto()
            }
            true
        }
        shareMenuView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.im_share_link -> requireContext().shareUrl(getPhotoItem().pageUrl)
                R.id.im_share_img -> shareImage()
            }
            toggleShareMenu()
            true
        }
    }

    private fun shareImage() = lifecycleScope.launch {
        requireContext().shareImage(getPhotoItem().large)
    }

    private fun savePhoto() = lifecycleScope.launch {
        val item = getPhotoItem()
        saveFileRequest.launch("${item.title}.jpg")
    }

    private fun showSnack(message: String) {
        root.snack(message) {
            anchorView = fab
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
        drawerToggle = object : ActionBarDrawerToggle(requireActivity(), drawerLayout, bottomToolbar, R.string.drawer_open, R.string.drawer_close) {

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

