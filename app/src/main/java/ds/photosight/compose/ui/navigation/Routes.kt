package ds.photosight.compose.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object GalleryRoute : NavKey

@Serializable
data class ViewerRoute(val photoId: Int, val index: Int) : NavKey
