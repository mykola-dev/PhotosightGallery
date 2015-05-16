package ds.photosight.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import ds.photosight.R;

//
// **********************************************************************************************************************************************
// ShareProgress
// **********************************************************************************************************************************************
//
public class ShareProgress extends AsyncTask<Intent, Object, Object> {
	ProgressDialog progress;
	Context ctx;


	public ShareProgress(Context ctx) {
		super();
		this.ctx = ctx;
	}


	@Override
	protected Object doInBackground(Intent... p) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ctx.startActivity(Intent.createChooser(p[0], ctx.getString(R.string.share_link)));

		return null;
	}


	@Override
	protected void onPreExecute() {
		progress = ProgressDialog.show(ctx, "", ctx.getString(R.string.loading));
		super.onPreExecute();
	}


	@Override
	protected void onPostExecute(Object result) {

		progress.dismiss();
		super.onPostExecute(result);
	}

}