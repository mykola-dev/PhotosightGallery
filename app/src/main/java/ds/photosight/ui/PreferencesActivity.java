package ds.photosight.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.Toast;
import ds.photosight.App;
import ds.photosight.Constants;
import ds.photosight.R;
import ds.photosight.utils.L;
import ds.photosight.utils.ShareProgress;
import ds.photosight.utils.Utils;

public class PreferencesActivity extends PreferenceActivity implements Constants, SharedPreferences.OnSharedPreferenceChangeListener {

	SharedPreferences prefs;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

	}



	@Override
	protected void onDestroy() {
		prefs.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences p, String key) {
		if (key.equals(PREFS_KEY_CACHE_PATH) || key.equals((PREFS_KEY_USE_INTERNAL_CACHE_DIR))) {
			L.v("cache setting changed");
			App.getInstance().setCacheDir();
		}


	}


	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (preference.getKey().equals("about")) {
			App.showAbout(this);
		} else if (preference.getKey().equals("shareApp")) {
			shareApp();
		} else if (preference.getKey().equals("donate")) {
			donate();
		} else if (preference.getKey().equals("clearCache")) {
			showClearCacheDialog();
		} else if (preference.getKey().equals("lowres")) {
			App.isLowRes = prefs.getBoolean("lowres", false);
		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}


	private void donate() {
		Toast.makeText(this, getString(R.string.coming_soon), 0).show();
	}


	private void showClearCacheDialog() {
		showDialog(1);
	}


	private void shareApp() {
		final Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app_subj));
		share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text) + " " + MARKETS_URL[CURRENT_MARKET]);
		new ShareProgress(this).execute(share);
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		final View view = null;
		final AlertDialog.Builder d = new AlertDialog.Builder(this);
		switch (id) {
			case 1:
				d.setTitle(R.string.clear_cache);
				d.setMessage(ds.photosight.R.string.do_you_want_to_clear_the_cache_now_);
				d.setIcon(android.R.drawable.ic_dialog_alert);
				d.setPositiveButton(android.R.string.yes, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Utils.clearCaches(getApplicationContext());

					}
				});
				d.setNegativeButton(android.R.string.no, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				break;
		}

		//d.setView(view);
		final AlertDialog diag = d.create();
		return diag;
	}

}
