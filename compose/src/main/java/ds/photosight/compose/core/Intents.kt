package ds.photosight.compose.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.core.content.FileProvider
import ds.photosight.compose.R
import java.io.File

class SaveImage : ActivityResultContract<String, Uri?>() {
    @CallSuper
    override fun createIntent(context: Context, input: String): Intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        .setType("image/jpeg")
        .putExtra(Intent.EXTRA_TITLE, input)
        .addCategory(Intent.CATEGORY_OPENABLE)

    override fun getSynchronousResult(context: Context, input: String): SynchronousResult<Uri?>? = null

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? =
        if (intent == null || resultCode != Activity.RESULT_OK) null
        else intent.data
}