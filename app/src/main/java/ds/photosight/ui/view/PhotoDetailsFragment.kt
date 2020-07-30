package ds.photosight.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ds.photosight.R
import ds.photosight.core.loadGlideBitmap
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.coroutines.launch


class PhotoDetailsFragment : BottomSheetDialogFragment() {

    private val args by navArgs<PhotoDetailsFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_details, container, false)

    override fun getTheme(): Int = R.style.AppTheme_BottomSheet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photo = args.photoInfo
        initHistogram(photo.thumb)
        titleLabel.text = photo.title
        authorLabel.text = photo.authorName
        sourceLabel.text = photo.pageUrl
    }

    private fun initHistogram(imageUrl: String) = lifecycleScope.launch {
        val bitmap = requireContext().loadGlideBitmap(imageUrl)
        histogram.setBitmap(bitmap)
    }
}