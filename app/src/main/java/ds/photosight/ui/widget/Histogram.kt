package ds.photosight.ui.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.androidquery.util.AQUtility
import ds.photosight.R
import ds.photosight.utils.L

public class Histogram(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var bitmap: Bitmap? = null
    private var result: Bitmap? = null
    private val paint: Paint
    private var mAnimator: ValueAnimator? = null
    private var alpha = 0


    init {

        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(Color.GRAY)
        paint.setTextSize(AQUtility.dip2pixel(context, 14f).toFloat())

        fillDemo()
    }


    private fun fillDemo() {

    }


    public fun setBitmap(b: Bitmap) {
        bitmap = b
        invalidate()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        L.v("w=$w h=$h")
        if (bitmap != null)
            process()
    }


    override fun onDraw(canvas: Canvas) {
        val w = getWidth()
        val h = getHeight()

        if (bitmap != null) {
            if (result == null) {
                // process
                process()
            } else {
                // draw
                if (mAnimator == null && alpha == 0)
                    startAnimator()

                paint.setAlpha(alpha)
                canvas.drawBitmap(result, 0f, 0f, paint)
            }

        }

        val text = getContext().getString(R.string.processing)
        paint.setAlpha(255 - alpha)
        canvas.drawText(text, (w.toFloat() - paint.measureText(text)) / 2, (h / 2).toFloat(), paint)

    }


    private fun startAnimator() {
        mAnimator = ValueAnimator.ofInt(0, 255)
        mAnimator!!.addUpdateListener({
                alpha = it.getAnimatedValue() as Int
                postInvalidate()
        })
        mAnimator!!.start()

    }


    public fun process() {
        AQUtility.postAsync( {
                L.v("process...")
                val r = IntArray(256)
                val g = IntArray(256)
                val b = IntArray(256)
                val pixels = IntArray(bitmap!!.getWidth() * bitmap!!.getHeight())
                bitmap!!.getPixels(pixels, 0, bitmap!!.getWidth(), 0, 0, bitmap!!.getWidth(), bitmap!!.getHeight())

                for (p in pixels) {
                    r[Color.red(p)]++
                    g[Color.green(p)]++
                    b[Color.blue(p)]++
                }

                val max = Math.max(Math.max(max(r), max(g)), max(b))
                val height = getHeight()
                val width = getWidth()
                val minFactor = height.toFloat() / 12000.toFloat()
                val xFactor = width.toFloat() / 256.toFloat()
                val yFactor = Math.max(height.toFloat() / max.toFloat(), minFactor)
                L.v("xFactor=" + xFactor + " yFactor=" + yFactor)

                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SCREEN))
                val pathR = Path()
                val pathG = Path()
                val pathB = Path()
                result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val c = Canvas(result)

                pathR.moveTo(0f, height.toFloat())
                pathG.moveTo(0f, height.toFloat())
                pathB.moveTo(0f, height.toFloat())

                for (i in 0..255 - 1) {

                    val x1 = i.toFloat() * xFactor
                    var y1 = height.toFloat() - r[i].toFloat() * yFactor
                    val x2 = (i + 1).toFloat() * xFactor
                    var y2 = height.toFloat() - r[i + 1].toFloat() * yFactor

                    pathR.quadTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2)

                    y1 = height.toFloat() - g[i].toFloat() * yFactor
                    y2 = height.toFloat() - g[i + 1].toFloat() * yFactor
                    pathG.quadTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2)

                    y1 = height.toFloat() - b[i].toFloat() * yFactor
                    y2 = height.toFloat() - b[i + 1].toFloat() * yFactor
                    pathB.quadTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2)

                }
                pathR.lineTo(width.toFloat(), height.toFloat())
                pathG.lineTo(width.toFloat(), height.toFloat())
                pathB.lineTo(width.toFloat(), height.toFloat())
                paint.setColor(Color.RED)
                c.drawPath(pathR, paint)
                paint.setColor(Color.GREEN)
                c.drawPath(pathG, paint)
                paint.setColor(Color.BLUE)
                c.drawPath(pathB, paint)

                postInvalidate()
        })
    }


    private fun max(values: IntArray): Int {
        var max = Integer.MIN_VALUE
        for (value in values) {
            if (value > max)
                max = value
        }
        return max
    }

}
