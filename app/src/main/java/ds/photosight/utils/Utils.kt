package ds.photosight.utils

import android.content.Context
import android.util.TypedValue
import com.androidquery.callback.BitmapAjaxCallback
import com.androidquery.util.AQUtility
import ds.photosight.App

import java.io.*

public class Utils {
    companion object {

        public fun copyFiles(src: File, dst: File): Boolean {
            var i: InputStream? = null
            var o: OutputStream? = null

            val dir = dst.getParentFile()
            if (!dir.exists())
                dir.mkdirs()

            try {
                i = FileInputStream(src)
                o = FileOutputStream(dst)
                AQUtility.copy(i, o)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            } finally {
                if (i != null && o != null) {
                    try {
                        o.close()
                        i.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }


        public fun clearCaches(c: Context) {
            AQUtility.cleanCacheAsync(c, 0, 0)
            BitmapAjaxCallback.clearCache()
        }


        public fun dp(dp: Int): Int =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), App.getInstance()!!.getResources().getDisplayMetrics()).toInt()
    }
}
