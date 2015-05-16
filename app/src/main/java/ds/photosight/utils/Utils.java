package ds.photosight.utils;

import android.content.Context;
import android.util.TypedValue;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import ds.photosight.App;

import java.io.*;

public class Utils {

	public static boolean copyFiles(File src, File dst) {
		InputStream is = null;
		OutputStream os = null;

		File dir = dst.getParentFile();
		if (!dir.exists())
			dir.mkdirs();

		try {
			is = new FileInputStream(src);
			os = new FileOutputStream(dst);
			AQUtility.copy(is, os);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (is != null && os != null) {
				try {
					os.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public static void clearCaches(Context c) {
		AQUtility.cleanCacheAsync(c, 0, 0);
		BitmapAjaxCallback.clearCache();
		//Toast.makeText(c, R.string.cache_cleared, 0).show();
	}


	public static int dp(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, App.getInstance().getResources().getDisplayMetrics());
	}
}
