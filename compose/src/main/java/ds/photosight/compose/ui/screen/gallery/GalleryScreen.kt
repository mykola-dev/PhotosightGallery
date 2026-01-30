@file:Suppress("MoveLambdaOutsideParentheses")

package ds.photosight.compose.ui.screen.gallery

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ds.photosight.compose.R
import ds.photosight.compose.repo.getIndexById
import ds.photosight.compose.ui.dialog.AboutDialog
import ds.photosight.compose.ui.events.UiEvent
import ds.photosight.compose.ui.isolate
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.pagedItems
import ds.photosight.compose.ui.rememberToolbarNestedScrollConnection
import ds.photosight.compose.ui.screen.MainViewModel
import ds.photosight.compose.util.ImagePreloader
import ds.photosight.compose.util.log
import ds.photosight.compose.util.logCompositions
import ds.photosight.compose.util.rememberDerived
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun GalleryScreen(
        mainViewModel: MainViewModel,
        gridState: LazyStaggeredGridState,
        onNavigateToViewer: (Int, Int) -> Unit,
) {
    logCompositions(msg = "root")
    val viewModel: GalleryViewModel = koinViewModel()
    mainViewModel.setMenuStateFlow(viewModel.menuStateFlow)

    val event: State<UiEvent?> = viewModel.events.collectAsState(null)

    val menuState by viewModel.menuStateFlow.collectAsState()
    val galleryState = viewModel.galleryState.collectAsState()
    val photosStream: LazyPagingItems<Photo> =
            mainViewModel.photosPagedFlow.collectAsLazyPagingItems()
    val selectedPhotoIndex = photosStream.getIndexById(mainViewModel.selectedId)

    isolate({ photosStream.loadState }) { state ->
        LaunchedEffect(state) {
            viewModel.updateLoadingState(state)
            viewModel.updateErrorState(state)
        }
    }

    // Track which photo is currently being preloaded
    var preloadingPhotoId by remember { mutableStateOf<Int?>(null) }

    val toolbarState = remember {
        derivedStateOf {
            ToolbarState(
                    galleryState.value.title,
                    galleryState.value.subtitle,
                    menuState.categoriesFilter,
                    viewModel::onShowAboutDialog,
                    viewModel::onFilterSelected,
                    viewModel::onSorterSelected
            )
        }
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    GalleryContent(
            photos = photosStream,
            gridState = gridState,
            menuState = menuState,
            galleryState = galleryState,
            selectedPhotoIndex = selectedPhotoIndex,
            preloadingPhotoId = preloadingPhotoId,
            onMenuItemSelected = { viewModel.onMenuSelected(it) },
            onPhotoClicked = { photo ->
                // Use current index if available, otherwise find it
                val index = photosStream.getIndexById(photo.id) ?: 0
                // Navigate immediately to eliminate lag
                onNavigateToViewer(photo.id, index)
                scope.launch {
                    // Preload full-size image in background for smoother swap once viewer opens
                    ImagePreloader.preload(context, photo.large)
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
}

@Composable
fun LoadingSlot(isLoading: Boolean) {
    if (isLoading) {
        logCompositions(msg = "loading")
        LinearProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryContent(
        photos: LazyPagingItems<Photo>,
        gridState: LazyStaggeredGridState,
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
    val hostState = scaffoldState.snackbarHostState

    val message = stringResource(id = R.string.loading_failed)
    val retryText = stringResource(id = R.string.retry)

    val context = LocalContext.current
    LaunchedEffect(event.value) {
        val e = event.value ?: return@LaunchedEffect
        log.v("on new event: $event")
        when (e) {
            is UiEvent.Toast -> Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            is UiEvent.Retry -> {
                val result = hostState.showSnackbar(message = message, actionLabel = retryText)
                when (result) {
                    SnackbarResult.Dismissed -> {}
                    SnackbarResult.ActionPerformed -> onRetry()
                }
            }
        }
    }

    var showMenu by remember { mutableStateOf(true) }
    val targetPeekHeight =
            if (showMenu && menuState.selectedItem != null) {
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 48.dp
            } else {
                0.dp
            }

    val sheetPeekHeight by animateDpAsState(targetValue = targetPeekHeight)

    BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetDragHandle = null,
            sheetContent = {
                BottomMenu(
                        scaffoldState.bottomSheetState,
                        menuState,
                        onMenuItemSelected,
                )
            },
            sheetPeekHeight = sheetPeekHeight,
            sheetContainerColor = Color.Transparent,
            sheetContentColor = MaterialTheme.colorScheme.onPrimary,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            snackbarHost = { SnackbarHost(hostState) }
    ) { innerPadding ->
        // Only apply top padding to the Box, allowing it to extend behind the bottom sheet
        Box(
                Modifier.fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding())
                        .nestedScroll(nestedScrollConnection)
        ) {
            LazyGrid(
                    GridState(
                            state = gridState,
                            // ... pass bottom padding to grid content padding instead
                            bottomPadding = innerPadding.calculateBottomPadding(),
                            nestedScrollConnection = nestedScrollConnection,
                            photos = photos,
                            selectedPhotoIndex = selectedPhotoIndex,
                            preloadingPhotoId = preloadingPhotoId,
                            onPhotoClicked = onPhotoClicked,
                            onFirstVisibleItem = onFirstVisibleItem,
                            onScrollingUp = { isScrollingUp -> showMenu = isScrollingUp }
                    )
            )
            MainToolbar(
                    state = toolbarState,
                    modifier =
                            Modifier.offset {
                                IntOffset(
                                        x = 0,
                                        y =
                                                nestedScrollConnection.toolbarOffsetHeightPx.value
                                                        .roundToInt()
                                )
                            },
            )

            loadingSlot()

            if (galleryState.value.showAboutDialog) {
                AboutDialog(onDismiss = onDismissAboutDialog)
            }
        }
    }
}

@Composable
private fun LazyGrid(gridState: GridState) =
        with(gridState) {
            logCompositions(msg = "lazy grid")

            val scrollingUp by state.isScrollingUp()
            LaunchedEffect(scrollingUp) {
                log.v("scroll direction: $scrollingUp")
                onScrollingUp(scrollingUp)
            }

            val firstItem =
                    rememberDerived(state) {
                        state.firstVisibleItemIndex.let {
                            if (photos.itemCount > it) photos[it] else null
                        }
                    }
            onFirstVisibleItem(firstItem)

            LaunchedEffect(selectedPhotoIndex) {
                if (selectedPhotoIndex != null && photos.itemCount > 0) {
                    val visibleItems = state.layoutInfo.visibleItemsInfo
                    if (visibleItems.isNotEmpty()) {
                        val first = visibleItems.minOf { it.index }
                        val last = visibleItems.maxOf { it.index }

                        if (selectedPhotoIndex !in first..last) {
                            log.d(
                                    "Selected photo $selectedPhotoIndex is out of viewport ($first..$last). Scrolling."
                            )
                            state.scrollToItem(selectedPhotoIndex)
                        }
                    }
                }
            }

            LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = state,
                    contentPadding =
                            PaddingValues(
                                    top = nestedScrollConnection.toolbarHeight,
                                    bottom =
                                            WindowInsets.navigationBars
                                                    .asPaddingValues()
                                                    .calculateBottomPadding() + bottomPadding
                            ),
            ) {
                pagedItems(photos) { item ->
                    val isPreloading = item.id == preloadingPhotoId
                    Thumb(item, onPhotoClicked, isPreloading)
                }
            }
        }

@Composable
private fun LazyStaggeredGridState.isScrollingUp(): State<Boolean> {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                        previousIndex > firstVisibleItemIndex
                    } else {
                        previousScrollOffset >= firstVisibleItemScrollOffset
                    }
                    .also {
                        previousIndex = firstVisibleItemIndex
                        previousScrollOffset = firstVisibleItemScrollOffset
                    }
        }
    }
}
