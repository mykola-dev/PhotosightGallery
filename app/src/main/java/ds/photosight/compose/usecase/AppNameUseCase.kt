package ds.photosight.compose.usecase

import android.content.Context

class AppNameUseCase(private val context: Context) {
    operator fun invoke(): String = context.applicationInfo.loadLabel(context.packageManager).toString()
}