package ds.photosight.view

import android.view.GestureDetector
import android.view.MotionEvent
import com.github.chrisbanes.photoview.PhotoView
import timber.log.Timber
import kotlin.math.min

class PhotoGestureListener(private var photo: PhotoView, val onClick: () -> Unit) : GestureDetector.OnDoubleTapListener {

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        println("onSingleTapConfirmed")
        onClick()

        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        println("onDoubleTap")

        try {
            val scale: Float
            val x = e.x
            val y = e.y

            val rect = photo.displayRect
            val w = rect.width().toInt()
            val h = rect.height().toInt()
            Timber.v("w=$w h=$h")

            scale = when {
                w < photo.width -> photo.width.toFloat() / (w.toFloat() + 2)    // extra 2 pixels for proper drag inside viewpager
                h < photo.height -> photo.height.toFloat() / (h.toFloat() + 2)
                else -> photo.minimumScale
            }

            photo.setScale(min(scale, photo.maximumScale), x, y, true)

        } catch (ex: ArrayIndexOutOfBoundsException) {
            // Can sometimes happen when getX() and getY() is called
            ex.printStackTrace()
        }

        return true
    }


    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        println("onDoubleTapEvent")
        return true
    }
}