package ds.photosight.utils;

import android.content.Context;
import android.widget.Toast;
import ds.photosight.App;

/**
 * Used for quick debugging toasts
 */
public class T {

	public static void show(Context c, String message) {
		if (L.DEBUG) {
			Toast.makeText(c, message, 0).show();
		}
	}


	public static void show(Context c, String message, Object... params) {
		if (L.DEBUG) {
			if (params == null || params.length == 0)
				return;

			show(c, String.format(message, params));
		}
	}


	public static void show(final String text) {
		show(App.getInstance(), text);
	}
}
