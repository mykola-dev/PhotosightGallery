package ds.photosight.ui

import java.io.Serializable

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import com.androidquery.util.AQUtility
import ds.photosight.Constants
import ds.photosight.R
import ds.photosight.ui.widget.Histogram
import ds.photosight.utils.L

public class InfoDialog(var data: Map<Int, String>?) : DialogFragment(), Constants {


    override fun onCreate(b: Bundle?) {
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Panel)

        if (b != null) {
            data = b.get("map") as Map<Int, String>
        }
        super<DialogFragment>.onCreate(b)
    }


    override fun onSaveInstanceState(b: Bundle?) {
        b!!.putSerializable("map", data as Serializable)
        super<DialogFragment>.onSaveInstanceState(b)
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val lp = getDialog().getWindow().getAttributes()
        lp.dimAmount = 0.4.toFloat()
        getDialog().getWindow().setAttributes(lp)
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        val v = inflater!!.inflate(R.layout.info, null)

        return v
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AnimatedDialog(getActivity(), getTheme())
    }


    override fun onViewCreated(v: View?, savedInstanceState: Bundle?) {
        super<DialogFragment>.onViewCreated(v, savedInstanceState)

        val table = v!!.findViewById(R.id.table) as LinearLayout
        val title = v.findViewById(R.id.titleText) as TextView
        title.setText(data?.get(Constants.DATA_IMG_NAME))

        // fetch bitmap
        fetchImage(data!!)

        table.setVisibility(View.INVISIBLE)
        val a = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_in)
        v.startAnimation(a)
        a.setAnimationListener(object : AnimationListener {

            override fun onAnimationStart(animation: Animation) {
            }


            override fun onAnimationRepeat(animation: Animation) {
            }


            override fun onAnimationEnd(animation: Animation) {
                table.setVisibility(View.VISIBLE)
                val a = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in)
                table.startAnimation(a)
            }
        })
    }


    private fun fetchImage(data: Map<Int, String>) {

        val aq = AQuery(getView())
        var b: Bitmap? = aq.getCachedImage(data.get(Constants.DATA_URL_LARGE))
        if (b == null)
            b = aq.getCachedImage(data.get(Constants.DATA_URL_SMALL))
        if (b != null)
            (aq.id(R.id.histogram).getView() as Histogram).setBitmap(b!!)
        else {
            AQUtility.time("fetch")
            aq.ajax<Bitmap>(data.get(Constants.DATA_URL_LARGE), javaClass<Bitmap>(), object : AjaxCallback<Bitmap>() {
                override fun callback(url: String?, b: Bitmap?, status: AjaxStatus?) {
                    super.callback(url, b, status)
                    if (b != null) {
                        L.v("bitmap fetched. width=" + b.getWidth())
                        AQUtility.timeEnd("fetch", 0)
                        (aq.id(R.id.histogram).getView() as Histogram).setBitmap(b)
                    }
                }
            })
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private class AnimatedDialog(context: Context, theme: Int) : Dialog(context, theme) {


        override fun onTouchEvent(event: MotionEvent): Boolean {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                finalAnimate()
                return true
            }

            return false

        }


        override fun onBackPressed() {
            finalAnimate()
        }


        public fun finalAnimate() {

            val v = findViewById(R.id.infoLayuot)
            val table = v.findViewById(R.id.table) as LinearLayout

            val a = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out)
            table.startAnimation(a)
            a.setAnimationListener(object : AnimationListener {

                override fun onAnimationStart(animation: Animation) {
                }


                override fun onAnimationRepeat(animation: Animation) {
                }


                override fun onAnimationEnd(animation: Animation) {
                    table.setVisibility(View.INVISIBLE)
                    val a = AnimationUtils.loadAnimation(getContext(), R.anim.anim_out)
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

