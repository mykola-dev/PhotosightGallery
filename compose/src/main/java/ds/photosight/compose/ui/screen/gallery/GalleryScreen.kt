@file:Suppress("MoveLambdaOutsideParentheses")

package ds.photosight.compose.ui.screen.gallery

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ds.photosight.compose.R
import ds.photosight.compose.repo.getIndexById
import ds.photosight.compose.ui.ToolbarNestedScrollConnection
import ds.photosight.compose.ui.dialog.AboutDialog
import ds.photosight.compose.ui.events.UiEvent
import ds.photosight.compose.ui.isolate
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.pagedItems
import ds.photosight.compose.ui.rememberToolbarNestedScrollConnection
import ds.photosight.compose.ui.screen.MainViewModel
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.util.ImagePreloader
import ds.photosight.compose.util.log
import ds.photosight.compose.util.logCompositions
import ds.photosight.compose.util.rememberDerived
import kotlin.math.roundToInt

@Composable
fun GalleryScreen(
    mainViewModel: MainViewModel,
    onNavigateToViewer: (Int) -> Unit,
) {
    logCompositions(msg = "root")
    val viewModel: GalleryViewModel = koinViewModel()
    mainViewModel.setMenuStateFlow(viewModel.menuStateFlow)

    val event: State<UiEvent?> = viewModel.events.collectAsState(null)

    val menuState by viewModel.menuStateFlow.collectAsState()
    val galleryState = viewModel.galleryState.collectAsState()
    val photosStream: LazyPagingItems<Photo> = mainViewModel.photosPagedFlow.collectAsLazyPagingItems()
    val selectedPhotoIndex = photosStream.getIndexById(mainViewModel.selectedId)

    isolate({ photosStream.loadState }) { state ->
        LaunchedEffect(state) {
            viewModel.updateLoadingState(state)
            viewModel.updateErrorState(state)
        }
    }

    // Track which photo is currently being preloaded
    var preloadingPhotoId by remember { mutableStateOf<Int?>(null) }

    val toolbarState = derivedStateOf {
        ToolbarState(
            galleryState.value.title,
            galleryState.value.subtitle,
            menuState.categoriesFilter,
            viewModel::onShowAboutDialog,
            viewModel::onFilterSelected,
            viewModel::onSorterSelected
        )
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    GalleryContent(
        photos = photosStream,
        menuState = menuState,
        galleryState = galleryState,
        selectedPhotoIndex = selectedPhotoIndex,
        preloadingPhotoId = preloadingPhotoId,
        onMenuItemSelected = { viewModel.onMenuSelected(it) },
        onPhotoClicked = { photo ->
            scope.launch {
                // Preload full-size image before navigating for smooth transition
                val success = ImagePreloader.preload(context, photo.large)
                if (success) {
                    onNavigateToViewer(photo.id)
                } else {
                    // Navigate anyway even if preload failed
                    onNavigateToViewer(photo.id)
                }
            }
        },
        event = event,
        onRetry = photosStream::retry,
        loadingSlot = { LoadingSlot(galleryState.value.isLoading) },
        onFirstVisibleItem = { state ->
            state.value?.let { viewModel.setFirstVisibleItem(it) }
        },
        toolbarState = toolbarState,
        onDismissAboutDialog = viewModel::onDismissAboutDialog,
    )

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(color = Palette.translucent)
    }
}


@Composable
fun LoadingSlot(isLoading: Boolean) {
    if (isLoading) {
        logCompositions(msg = "loading")
        LinearProgressIndicator(color = MaterialTheme.colors.secondary, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun GalleryContent(
    photos: LazyPagingItems<Photo>,
    galleryState: State<GalleryState>,
    menuState: MenuState,
    selectedPhotoIndex: Int?,
    preloadingPhotoId: Int?,
    event: State<UiEvent?>,
    onMenuItemSelected: (MenuItemState) -> Unit,
    onPhotoClicked: (Photo) -> Unit,
    onRetry: () -> Unit,
    loadingSlot: @Composable () -> Unit,
    onFirstVisibleItem: @Composable (State<Photo?>) -> Unit,
    toolbarState: State<ToolbarState>,
    onDismissAboutDialog: () -> Unit,
) {
    logCompositions(msg = "gallery content")

    val nestedScrollConnection = rememberToolbarNestedScrollConnection()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val snackbarState = scaffoldState.snackbarHostState

    val message = stringResource(id = R.string.loading_failed)
    val retryText = stringResource(id = R.string.retry)

    val context = LocalContext.current
    LaunchedEffect(event.value) {
        val e = event.value ?: return@LaunchedEffect
        log.v("on new event: $event")
        when (e) {
            is UiEvent.Toast -> Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            is UiEvent.Retry -> {
                val result = snackbarState.showSnackbar(
                    message = message,
                    actionLabel = retryText
                )
                when (result) {
                    SnackbarResult.Dismissed -> {}
                    SnackbarResult.ActionPerformed -> onRetry()
                }
            }
        }

    }

    var showMenu by remember { mutableStateOf(true) }
    val shitPeekHeight = if (showMenu && menuState.selectedItem != null) WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 48.dp
    else 0.dp

    BottomSheetScaffold(
        sheetContent = {
            BottomMenu(
                scaffoldState.bottomSheetState,
                menuState,
                onMenuItemSelected,
            )
        },
        scaffoldState = scaffoldState,
        sheetBackgroundColor = MaterialTheme.colors.primary,
        sheetShape = MaterialTheme.shapes.large,
        sheetContentColor = MaterialTheme.colors.surface,
        sheetPeekHeight = shitPeekHeight,
    ) {

        Box(
            Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {

            LazyGrid(GridState(
                nestedScrollConnection = nestedScrollConnection,
                photos = photos,
                selectedPhotoIndex = selectedPhotoIndex,
                preloadingPhotoId = preloadingPhotoId,
                onPhotoClicked = onPhotoClicked,
                onFirstVisibleItem = onFirstVisibleItem,
                onScrollingUp = { scrollingUp -> showMenu = scrollingUp }
            )
            )
            MainToolbar(
                state = toolbarState,
                modifier = Modifier.offset { IntOffset(x = 0, y = nestedScrollConnection.toolbarOffsetHeightPx.value.roundToInt()) },
            )

            loadingSlot()

            if (galleryState.value.showAboutDialog) {
                AboutDialog(onDismiss = onDismissAboutDialog)
            }

        }
    }
}

@Composable
private fun LazyGrid(gridState: GridState) = with(gridState) {
    logCompositions(msg = "lazy grid")
    val state = rememberLazyStaggeredGridState()

    val scrollingUp by state.isScrollingUp()
    LaunchedEffect(scrollingUp) {
        log.v("scroll direction: $scrollingUp")
        onScrollingUp(scrollingUp)
    }

    val firstItem = rememberDerived(state) {
        state.firstVisibleItemIndex.let {
            if (photos.itemCount > it) photos[it] else null
        }
    }
    onFirstVisibleItem(firstItem)

    LaunchedEffect(selectedPhotoIndex) {
        selectedPhotoIndex?.let { index ->
            val isVisible = state.layoutInfo.visibleItemsInfo.any { it.index == index }
            if (!isVisible) {
                state.scrollToItem(index)
            }
        }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = state,
        contentPadding = PaddingValues(top = nestedScrollConnection.toolbarHeight, bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
    ) {

        pagedItems(photos) { item ->
            val isPreloading = item.id == preloadingPhotoId
            Thumb(item, onPhotoClicked, isPreloading)
        }

    }

}

@Composable
private fun LazyStaggeredGridState.isScrollingUp(): State<Boolean> {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }
}

data class GridState(
    val nestedScrollConnection: ToolbarNestedScrollConnection,
    val photos: LazyPagingItems<Photo>,
    val selectedPhotoIndex: Int?,
    val preloadingPhotoId: Int?,
    val onPhotoClicked: (Photo) -> Unit,
    val onFirstVisibleItem: @Composable (State<Photo?>) -> Unit,
    val onScrollingUp: (Boolean) -> Unit,
)
