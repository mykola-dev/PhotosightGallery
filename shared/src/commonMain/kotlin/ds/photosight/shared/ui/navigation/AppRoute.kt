package ds.photosight.shared.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute {
    @Serializable
    data object Gallery : AppRoute

    @Serializable
    data class Viewer(val photoId: Int, val index: Int) : AppRoute
}
