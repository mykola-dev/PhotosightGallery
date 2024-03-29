package ds.photosight.compose.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ds.photosight.compose.R
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.screen.gallery.MenuState
import javax.inject.Inject

class ToolbarDataUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appNameProvider: AppNameUseCase,
) {

    fun getTitle(menuState: MenuState? = null): String = menuState?.selectedItem?.title ?: appNameProvider()

    fun getSubtitle(photoInfo: Photo? = null): String? = photoInfo
        ?.paginationKey
        ?.let { key ->
            if ("/" in key) key
            else context.getString(R.string.page_, key)
        }

}