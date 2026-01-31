package ds.photosight.compose.ui.screen.gallery

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.unit.Dp
import androidx.paging.compose.LazyPagingItems
import ds.photosight.compose.ui.ToolbarNestedScrollConnection
import ds.photosight.compose.ui.model.Photo

data class GridState(
    val state: LazyStaggeredGridState,
    val bottomPadding: Dp,
    val nestedScrollConnection: ToolbarNestedScrollConnection,
    val photos: LazyPagingItems<Photo>,
    val selectedPhotoIndex: Int?,
    val preloadingPhotoId: Int?,
    val onPhotoClicked: (Photo) -> Unit,
    val onFirstVisibleItem: @Composable (State<Photo?>) -> Unit,
    val onScrollingUp: (Boolean) -> Unit,
)
