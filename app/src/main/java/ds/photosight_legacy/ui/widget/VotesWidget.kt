package ds.photosight_legacy.ui.widget

/*

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.androidquery.AQuery
import ds.photosight.R
import ds.photosight_legacy.utils.Utils

class VotesWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var mRates: List<Int>? = null
    private var mAvards: List<String>? = null
    private val aq: AQuery = AQuery(this)
    private val mRateViews = arrayOfNulls<TextView>(5)
    private var mGreenBar: View? = null
    private var mRedBar: View? = null


    fun setRates(rates: List<Int>) {
        mRates = rates
    }


    fun setAvards(avards: List<String>) {
        mAvards = avards
    }


    @SuppressLint("StaticFieldLeak")
    fun runAnimations() {

        awardIconSize = getAwardIconSize(mAvards!!.size)
        mRateViews[0] = aq.id(R.id.aRate).textView
        mRateViews[1] = aq.id(R.id.oRate).textView
        mRateViews[2] = aq.id(R.id.tRate).textView
        mRateViews[3] = aq.id(R.id.lRate).textView
        mRateViews[4] = aq.id(R.id.dRate).textView
        for (t in mRateViews) {
            t?.text = ""
        }

        mGreenBar = aq.id(R.id.greenBar).view
        mRedBar = aq.id(R.id.redBar).view
        (aq.id(R.id.labelsContainer).view as ViewGroup).startLayoutAnimation()
        removeAvards()

        object : AsyncTask<List<Int>, Int, Boolean>() {

            override fun doInBackground(params: Array<List<Int>>): Boolean? {
                val interpolator = AccelerateDecelerateInterpolator()
                val values = params[0]
                val rating = 1 / (values[3] + values[4]).toFloat() * values[3].toFloat()

                val results = arrayOfNulls<Int>(values.size + 1)
                val start = System.currentTimeMillis()
                var escape = false
                while (!escape) {
                    try {
                        Thread.sleep(10)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }


                    val f = (System.currentTimeMillis() - start).toFloat() / ANIMATION_DURATION
                    if (f >= 1)
                        escape = true

                    for (i in values.indices) {
                        results[i] = (values[i].toFloat() * interpolator.getInterpolation(f)).toInt()
                    }

                    //L.v("publish progress");
                    results[results.size - 1] = (rating * interpolator.getInterpolation(f) * 1000).toInt()
                    publishProgress(*results)

                }

                for (i in values.indices) {
                    results[i] = (values[i])
                }
                publishProgress(*results)

                return true
            }


            override fun onProgressUpdate(values: Array<Int>?) {
                for (i in 0 until values!!.size - 1) {
                    mRateViews[i]?.text = values[i].toString()
                }

                val height = values[values.size - 1] * mRedBar!!.height / 1000
                mGreenBar!!.layoutParams.height = height
            }


            override fun onPostExecute(o: Boolean) {
                if (mAvards != null)
                    addAvards()
            }
        }.execute(mRates)
    }


    private fun getAwardIconSize(size: Int): Int = when {
        size > 7 -> Utils.dp(16)
        size > 4 -> Utils.dp(24)
        else -> Utils.dp(32)
    }


    private fun removeAvards() {
        (aq.id(R.id.avardsContainer).view as ViewGroup).removeAllViews()
    }


    private fun addAvards() {
        val vg = aq.id(R.id.avardsContainer).view as ViewGroup

        val lp = LayoutParams(awardIconSize, awardIconSize)

        for (a in mAvards!!) {
            val img = ImageView(context)
            vg.addView(img)
            img.layoutParams = lp

            img.post { aq.id(img).image(a, true, true, 0, 0, null, R.anim.award_anim) }
        }
    }

    companion object {

        private val ANIMATION_DURATION = 1500
        private var awardIconSize: Int = 0// = Utils.dp(16);
    }


}
*/
