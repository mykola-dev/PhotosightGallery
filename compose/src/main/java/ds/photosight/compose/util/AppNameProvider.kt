package ds.photosight.compose.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppNameProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(): String = context.applicationInfo.loadLabel(context.packageManager).toString()
}