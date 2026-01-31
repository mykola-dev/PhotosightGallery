package ds.photosight.repo

import android.content.res.Resources
import androidx.annotation.StringRes

class ResourcesRepo(private val resources: Resources) {
    fun getString(@StringRes id: Int): String = resources.getString(id)

}
