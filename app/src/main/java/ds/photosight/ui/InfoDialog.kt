package ds.photosight.ui

import java.io.Serializable

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import com.androidquery.util.AQUtility
import ds.photosight.Constants
import ds.photosight.R
import ds.photosight.ui.widget.Histogram
import ds.photosight.utils.L

class InfoDialog(var data: Map<Int, String>?) : DialogFragment(), Constants {


    override fun onCreate(b: Bundle?) {
        setStyle(STYLE_NORMAL, android.R.style.Theme_Panel)

        if (b != null) {
            data = b.get("map") as Map<Int, String>
        }
        super.onCreate(b)
    }


    override fun onSaveInstanceState(b: Bundle) {
        b.putSerializable("map", data as Serializable)
        super.onSaveInstanceState(b)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val lp = dialog!!.window.attributes
        lp.dimAmount = 0.4.toFloat()
        dialog!!.window.attributes = lp
        dialog!!.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        val v = inflater.inflate(R.layout.info, null)

        return v
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AnimatedDialog(activity!!, theme)
    }


    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        val table = v.findViewById(R.id.table) as LinearLayout
        val title = v.findViewById(R.id.titleText) as TextView
        title.text = data?.get(Constants.DATA_IMG_NAME)

        // fetch bitmap
        fetchImage(data!!)

        table.visibility = View.INVISIBLE
        val a = AnimationUtils.loadAnimation(activity, R.anim.anim_in)
        v.startAnimation(a)
        a.setAnimationListener(object : AnimationListener {

            override fun onAnimationStart(animation: Animation) {
            }


            override fun onAnimationRepeat(animation: Animation) {
            }


            override fun onAnimationEnd(animation: Animation) {
                table.visibility = View.VISIBLE
                val a = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
                table.startAnimation(a)
            }
        })
    }


    private fun fetchImage(data: Map<Int, String>) {

        val aq = AQuery(view)
        var b: Bitmap? = aq.getCachedImage(data[Constants.DATA_URL_LARGE])
        if (b == null)
            b = aq.getCachedImage(data[Constants.DATA_URL_SMALL])
        if (b != null)
            (aq.id(R.id.histogram).view as Histogram).setBitmap(b)
        else {
            AQUtility.time("fetch")
            aq.ajax(data[Constants.DATA_URL_LARGE], Bitmap::class.java, object : AjaxCallback<Bitmap>() {
                override fun callback(url: String?, b: Bitmap?, status: AjaxStatus?) {
                    super.callback(url, b, status)
                    if (b != null) {
                        L.v("bitmap fetched. width=" + b.width)
                        AQUtility.timeEnd("fetch", 0)
                        (aq.id(R.id.histogram).view as Histogram).setBitmap(b)
                    }
                }
            })
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private class AnimatedDialog(context: Context, theme: Int) : Dialog(context, theme) {


        override fun onTouchEvent(event: MotionEvent): Boolean {

            if (event.action == MotionEvent.ACTION_DOWN) {
                finalAnimate()
                return true
            }

            return false

        }


        override fun onBackPressed() {
            finalAnimate()
        }


        fun finalAnimate() {

            val v: View = findViewById(R.id.infoLayuot)
            val table = v.findViewById(R.id.table) as LinearLayout

            val a = AnimationUtils.loadAnimation(context, R.anim.fade_out)
            table.startAnimation(a)
            a.setAnimationListener(object : AnimationListener {

                override fun onAnimationStart(animation: Animation) {
                }


                override fun onAnimationRepeat(animation: Animation) {
                }


                override fun onAnimationEnd(animation: Animation) {
                    table.visibility = View.INVISIBLE
                    val a = AnimationUtils.loadAnimation(context, R.anim.anim_out)
                    v.startAnimation(a)
                    a.setAnimationListener(object : AnimationListener {

                        override fun onAnimationStart(animation: Animation) {
                        }


                        override fun onAnimationRepeat(animation: Animation) {
                        }


                        override fun onAnimationEnd(animation: Animation) {
                            v.post(object : Runnable {
                                override fun run() {
                                    dismiss()
                                }
                            })
                        }
                    })
                }
            })
        }

    }


}

