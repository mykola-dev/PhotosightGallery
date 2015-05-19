package ds.photosight.utils

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import ds.photosight.R
import kotlin.properties.Delegates

//
// **********************************************************************************************************************************************
// ShareProgress
// **********************************************************************************************************************************************
//
public class ShareProgress(var ctx: Context) : AsyncTask<Intent, Any, Any>() {
    var progress: ProgressDialog by Delegates.notNull()


    override fun doInBackground(vararg p: Intent): Any? {
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        ctx.startActivity(Intent.createChooser(p[0], ctx.getString(R.string.share_link)))

        return null
    }


    override fun onPreExecute() {
        progress = ProgressDialog.show(ctx, "", ctx.getString(R.string.loading))
        super.onPreExecute()
    }


    override fun onPostExecute(result: Any) {

        progress.dismiss()
        super.onPostExecute(result)
    }

}