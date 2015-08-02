package ds.photosight.ui.widget


import android.content.Context
import android.os.AsyncTask
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.androidquery.AQuery
import ds.photosight.R
import ds.photosight.utils.Utils

SuppressWarnings("ALL")
public class VotesWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var mRates: List<Int>? = null
    private var mAvards: List<String>? = null
    private val aq: AQuery
    private val mRateViews = arrayOfNulls<TextView>(5)
    private var mGreenBar: View? = null
    private var mRedBar: View? = null


    init{
        aq = AQuery(this)

    }


    public fun setRates(rates: List<Int>) {
        mRates = rates
    }


    public fun setAvards(avards: List<String>) {
        mAvards = avards
    }


    public fun runAnimations() {

        awardIconSize = getAwardIconSize(mAvards!!.size())
        mRateViews[0] = aq.id(R.id.aRate).getTextView()
        mRateViews[1] = aq.id(R.id.oRate).getTextView()
        mRateViews[2] = aq.id(R.id.tRate).getTextView()
        mRateViews[3] = aq.id(R.id.lRate).getTextView()
        mRateViews[4] = aq.id(R.id.dRate).getTextView()
        for (t in mRateViews) {
            t?.setText("")
        }

        mGreenBar = aq.id(R.id.greenBar).getView()
        mRedBar = aq.id(R.id.redBar).getView()
        (aq.id(R.id.labelsContainer).getView() as ViewGroup).startLayoutAnimation()
        removeAvards()

        object : AsyncTask<List<Int>, Int, Boolean>() {

            override fun doInBackground(params: Array<List<Int>>): Boolean? {
                val interpolator = AccelerateDecelerateInterpolator()
                val values = params[0]
                val rating = 1 / (values.get(3) + values.get(4)).toFloat() * values.get(3).toFloat()

                val results = arrayOfNulls<Int>(values.size() + 1)
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
                        results[i] = (values.get(i).toFloat() * interpolator.getInterpolation(f)).toInt()
                    }

                    //L.v("publish progress");
                    results[results.size() - 1] = (rating * interpolator.getInterpolation(f) * 1000).toInt()
                    publishProgress(*results)

                }

                for (i in values.indices) {
                    results[i] = (values.get(i))
                }
                publishProgress(*results)

                return true
            }


            override fun onProgressUpdate(values: Array<Int>?) {
                for (i in 0..values!!.size() - 1 - 1) {
                    mRateViews[i]?.setText(values[i].toString())
                }

                val height = values[values.size() - 1] * mRedBar!!.getHeight() / 1000
                //L.v("h=" + height);
                mGreenBar!!.getLayoutParams().height = height
                //mGreenBar.setMinimumHeight();
            }


            override fun onPostExecute(o: Boolean) {
                if (mAvards != null)
                    addAvards()
            }
        }.execute(mRates)
    }


    private fun getAwardIconSize(size: Int): Int {
        if (size > 7)
            return Utils.dp(16)
        else if (size > 4)
            return Utils.dp(24)
        else
            return Utils.dp(32)
    }


    private fun removeAvards() {
        (aq.id(R.id.avardsContainer).getView() as ViewGroup).removeAllViews()
    }


    private fun addAvards() {
        val vg = aq.id(R.id.avardsContainer).getView() as ViewGroup

        val lp = LinearLayout.LayoutParams(awardIconSize, awardIconSize)

        for (a in mAvards!!) {
            val img = ImageView(getContext())
            vg.addView(img)
            img.setLayoutParams(lp)

            val url = a
            img.post(object : Runnable {
                override fun run() {
                    aq.id(img).image(url, true, true, 0, 0, null, R.anim.award_anim)

                }
            })
        }
    }

    companion object {

        private val ANIMATION_DURATION = 1500
        private var awardIconSize: Int = 0// = Utils.dp(16);
    }


}
