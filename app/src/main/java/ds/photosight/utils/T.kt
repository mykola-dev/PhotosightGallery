package ds.photosight.utils

import android.content.Context
import android.widget.Toast
import ds.photosight.App

/**
 * Used for quick debugging toasts
 */
public class T {
    companion object {

        public fun show(c: Context, message: String) {
            if (L.DEBUG) {
                Toast.makeText(c, message, 0).show()
            }
        }


        public fun show(c: Context, message: String, vararg params: Any) {
            if (L.DEBUG) {
                if (params == null || params.size() == 0)
                    return

                show(c, java.lang.String.format(message, *params))
            }
        }


        public fun show(text: String) {
            show(App.getInstance()!!, text)
        }
    }
}
