package ds.photosight.ui.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import ds.photosight.R
import ds.photosight.utils.L
import kotlin.math.max

class Histogram(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var bitmap: Bitmap? = null
    private var result: Bitmap? = null
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mAnimator: ValueAnimator? = null
    private var alpha = 0

    init {

        paint.color = Color.GRAY
        //paint.textSize = AQUtility.dip2pixel(context, 14f).toFloat()

        fillDemo()
    }

    private fun fillDemo() {

    }

    fun setBitmap(b: Bitmap) {
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
        val w = width
        val h = height

        if (bitmap != null) {
            if (result == null) {
                // process
                process()
            } else {
                // draw
                if (mAnimator == null && alpha == 0)
                    startAnimator()

                paint.alpha = alpha
                canvas.drawBitmap(result, 0f, 0f, paint)
            }

        }

        val text = context.getString(R.string.processing)
        paint.alpha = 255 - alpha
        canvas.drawText(text, (w.toFloat() - paint.measureText(text)) / 2, (h / 2).toFloat(), paint)

    }


    private fun startAnimator() {
        mAnimator = ValueAnimator.ofInt(0, 255)
        mAnimator!!.addUpdateListener {
            alpha = it.animatedValue as Int
            postInvalidate()
        }
        mAnimator!!.start()

    }


    private fun process() {
        //AQUtility.postAsync {
            L.v("process...")
            val r = IntArray(256)
            val g = IntArray(256)
            val b = IntArray(256)
            val pixels = IntArray(bitmap!!.width * bitmap!!.height)
            bitmap!!.getPixels(pixels, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)

            for (p in pixels) {
                r[Color.red(p)]++
                g[Color.green(p)]++
                b[Color.blue(p)]++
            }

            val max = max(max(max(r), max(g)), max(b))
            val height = height
            val width = width
            val minFactor = height.toFloat() / 12000.toFloat()
            val xFactor = width.toFloat() / 256.toFloat()
            val yFactor = max(height.toFloat() / max.toFloat(), minFactor)
            L.v("xFactor=$xFactor yFactor=$yFactor")

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SCREEN)
            val pathR = Path()
            val pathG = Path()
            val pathB = Path()
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val c = Canvas(result)

            pathR.moveTo(0f, height.toFloat())
            pathG.moveTo(0f, height.toFloat())
            pathB.moveTo(0f, height.toFloat())

            for (i in 0 until 255) {

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
            paint.color = Color.RED
            c.drawPath(pathR, paint)
            paint.color = Color.GREEN
            c.drawPath(pathG, paint)
            paint.color = Color.BLUE
            c.drawPath(pathB, paint)

            postInvalidate()
        //}
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
