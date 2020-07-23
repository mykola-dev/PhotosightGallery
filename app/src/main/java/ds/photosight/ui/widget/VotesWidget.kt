package ds.photosight.ui.widget

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import ds.photosight.R
import ds.photosight.utils.dp
import kotlinx.android.synthetic.main.votes.view.*

class VotesWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    companion object {
        private const val ANIMATION_DURATION = 2000L
    }

    data class Stats(
        val views: Int = 0,
        val art: Int = 0,
        val original: Int = 0,
        val tech: Int = 0,
        val likes: Int = 0,
        val dislikes: Int = 0
    ) {
        var rating: Float = 1 / (likes + dislikes).toFloat() * likes.toFloat()
    }

    private var stats: Stats = Stats()

    @DrawableRes
    private var awardsResIds: List<Int> = emptyList()

    init {
        View.inflate(context, R.layout.votes, this)
    }

    fun init(stats: Stats, @DrawableRes awards: List<Int>) {
        this.stats = stats
        this.awardsResIds = awards
        awardsContainer.isVisible = false
        awardsContainer.removeAllViews()
    }

    fun runAnimations() {
        labelsContainer.startLayoutAnimation()

        ValueAnimator.ofFloat(0f, 1f)
            .setDuration(ANIMATION_DURATION)
            .apply {
                interpolator = DecelerateInterpolator(1.5f)
                addUpdateListener {
                    val value = it.animatedValue as Float
                    updateView(value)

                }
            }
            .start()

        if (awardsResIds.isNotEmpty()) addAwards()

    }

    private fun updateView(factor: Float) {
        fun Int.asText(factor: Float) = (this * factor).toInt().toString()

        views.text = stats.views.asText(factor)
        aRate.text = stats.art.asText(factor)
        oRate.text = stats.original.asText(factor)
        tRate.text = stats.tech.asText(factor)
        lRate.text = stats.likes.asText(factor)
        dRate.text = stats.dislikes.asText(factor)

        val height = stats.rating * factor * redBar.height
        greenBar.layoutParams.height = height.toInt()
    }


    private fun addAwards() {
        for (id in awardsResIds) {
            val img = ImageView(context)
            img.setImageDrawable(context.getDrawable(id))
            img.updatePadding(bottom = 4.dp)
            awardsContainer.addView(img)
            //img.layoutParams = lp
        }
        awardsContainer.isVisible = true
    }


}
