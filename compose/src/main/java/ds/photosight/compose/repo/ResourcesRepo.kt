package ds.photosight.repo

import android.content.res.Resources
import androidx.annotation.StringRes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourcesRepo @Inject constructor(private val resources: Resources) {
    fun getString(@StringRes id: Int): String = resources.getString(id)

}
