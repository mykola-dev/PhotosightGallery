package ds.photosight.view

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.R
import ds.photosight.model.asViewModel
import ds.photosight.parser.PhotoDetails
import ds.photosight.utils.recyclerView
import ds.photosight.view.adapter.SimpleAdapter
import ds.photosight.view.adapter.SimpleViewHolder
import ds.photosight.view.adapter.ViewerAdapter
import ds.photosight.view.widget.VotesWidget
import ds.photosight.viewmodel.CommentsState
import ds.photosight.viewmodel.MainViewModel
import ds.photosight.viewmodel.ViewerViewModel
import kotlinx.android.synthetic.main.fragment_viewer.*
import kotlinx.android.synthetic.main.item_comment.*
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
        toggleActionBar(false)

        setupViewPager()

    }

    private fun setupViewPager() {
        val adapter = ViewerAdapter(transitionHelper) {
            log.v("on clicked")
            toggleActionBar(!isActionBarVisible())
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
                findNavController().previousBackStackEntry?.savedStateHandle?.set("position", position)
                viewModel.savedStateHandle.set("position", position)
                val item = adapter.getItemAt(position)
                toolbar.title = item.title
                toolbar.subtitle = item.authorName
                val photoView = viewPager.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<View>(R.id.photoImage) ?: return
                transitionHelper.setupEnterCallback(photoView)
            }
        })
    }

    private fun isActionBarVisible(): Boolean = (activity as AppCompatActivity).supportActionBar?.isShowing ?: false

    private fun setupDrawer() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        drawerToggle = object : ActionBarDrawerToggle(requireActivity(), drawerLayout, toolbar, 0, 0) {

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                val photoId = (viewPager.adapter as ViewerAdapter).getItemAt(viewModel.position).id
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

    private val actionBar: ActionBar? get() = (activity as AppCompatActivity).supportActionBar

    private fun toggleActionBar(show: Boolean) {
        val ab = (activity as AppCompatActivity).supportActionBar
        if (show) {
            ab?.show()
        } else {
            ab?.hide()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
        Timber.v("onConfigurationChanged")
    }
}

class PhotoStatsAdapter(data: PhotoDetails) : SimpleAdapter<PhotoDetails>(R.layout.item_photo_stats, listOf(data)) {
    override fun onBind(holder: SimpleViewHolder, item: PhotoDetails, position: Int) {
        val votesView = holder.itemView as VotesWidget
        votesView.init(
            item.stats.asViewModel(),
            item.awards.map { votesView.resources.getIdentifier(it.toString(), "drawable", context.packageName) }
        )
        votesView.runAnimations()
    }

}

class CommentsAdapter(comments: List<PhotoDetails.Comment>) : SimpleAdapter<PhotoDetails.Comment>(R.layout.item_comment, comments) {

    override fun onBind(holder: SimpleViewHolder, item: PhotoDetails.Comment, position: Int) {
        holder.name.text = if (item.isAuthor) context.getString(R.string.photo_author)
        else item.author
        holder.content.text = item.text
        holder.date.text = DateUtils.getRelativeDateTimeString(context, item.timestamp, 0, DateUtils.DAY_IN_MILLIS, 0)
        holder.rating.text = if (item.likes != 0) "+${item.likes}" else ""
        Glide.with(holder.itemView)
            .load(item.avatar)
            .circleCrop()
            .error(R.drawable.anonymous)
            .into(holder.avatar)
    }

}

