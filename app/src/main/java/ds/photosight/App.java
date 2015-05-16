package ds.photosight;

import java.io.File;
import java.lang.reflect.Field;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import ds.photosight.utils.L;

public class App extends Application implements Constants {

	public static boolean isLowRes;
	private static SharedPreferences prefs;
	private static App instance;


	@Override
	public void onCreate() {
		super.onCreate();
		if (instance == null)
			instance = this;
		if (prefs == null)
			prefs = PreferenceManager.getDefaultSharedPreferences(this);

		isLowRes = prefs.getBoolean("lowres", false);

		forceOverflowMenu(true);

		aqueryConfig();
	}


	private void aqueryConfig() {
		//set the max number of images (image width > 50) to be cached in memory, default is 20
		BitmapAjaxCallback.setCacheLimit(100);
		BitmapAjaxCallback.setIconCacheLimit(50);

		//set the max size of an image to be cached in memory, default is 1600 pixels (ie. 400x400)
		BitmapAjaxCallback.setPixelLimit(500 * 500);


		//set the max size of the memory cache, default is 1M pixels (4MB)
		BitmapAjaxCallback.setMaxPixelLimit((int) getTotalMemorySize());

		setCacheDir();
	}


	public void setCacheDir() {
		File dir;
		if (!getPrefs().getBoolean(Constants.PREFS_KEY_USE_INTERNAL_CACHE_DIR, true)) {
			String extPath = getPrefs().getString(PREFS_KEY_CACHE_PATH, getString(R.string.default_cache_dir));
			dir = new File(Environment.getExternalStorageDirectory(), extPath);
		} else {
			dir = new File(getCacheDir(), "aquery");
		}

		L.v("setting cache dir " + dir);
		AQUtility.setCacheDir(dir);
	}


	public long getTotalMemorySize() {

		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		long availableMegs = mi.availMem / 2;
		L.v("total memory=" + availableMegs);
		return availableMegs;

	}


	@Override
	public void onLowMemory() {
		BitmapAjaxCallback.clearCache();
		L.e("LOW MEMORY!");
	}


	public void forceOverflowMenu(boolean enabled) {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, !enabled);
			}
		} catch (Exception ex) {
			// Ignore
		}
	}


	public boolean isPortrait() {
		return getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
	}


	public static SharedPreferences getPrefs() {
		return prefs;
	}


	public static App getInstance() {
		return instance;
	}


	public static void showAbout(Context ctx) {
		String ver;
		try {
			PackageInfo manager = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			ver = manager.versionName;
		} catch (NameNotFoundException e1) {
			ver = "100500";
		}

		final Dialog dialog = new Dialog(ctx, android.R.style.Theme_Dialog);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.about);
		final Button b = (Button) dialog.findViewById(R.id.aboutOK);
		final TextView text = (TextView) dialog.findViewById(R.id.aboutText);
		final TextView verText = (TextView) dialog.findViewById(R.id.versionText);

		String s = String.format(ctx.getResources().getString(R.string.abouttext), ctx.getResources().getString(R.string.changelog),
				PRO_VERSION ? MARKETS_URL_PRO[CURRENT_MARKET] : MARKETS_URL[CURRENT_MARKET]);
		text.setText(s);
		verText.setText(ver);
		b.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		dialog.getWindow().setWindowAnimations(android.R.style.Animation_Translucent);
		dialog.show();
	}

}
