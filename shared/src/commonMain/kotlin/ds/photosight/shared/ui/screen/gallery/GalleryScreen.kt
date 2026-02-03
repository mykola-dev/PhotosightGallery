@file:Suppress("MoveLambdaOutsideParentheses")

package ds.photosight.shared.ui.screen.gallery

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.LocalPlatformContext
import ds.photosight.shared.ui.dialog.AboutDialog
import ds.photosight.shared.ui.events.UiEvent
import ds.photosight.shared.ui.getIndexById
import ds.photosight.shared.ui.isolate
import ds.photosight.shared.ui.model.Photo
import ds.photosight.shared.ui.pagedItems
import ds.photosight.shared.ui.rememberToolbarNestedScrollConnection
import ds.photosight.shared.ui.screen.MainViewModel
import ds.photosight.shared.util.ImagePreloader
import ds.photosight.shared.util.log
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import photosight.shared.generated.resources.Res
import photosight.shared.generated.resources.loading_failed
import photosight.shared.generated.resources.retry
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    mainViewModel: MainViewModel,
    gridState: LazyStaggeredGridState,
    onNavigateToViewer: (Int, Int) -> Unit,
) {
    val viewModel = koinViewModel<GalleryViewModel>()
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
    val context = LocalPlatformContext.current

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingSlot(isLoading: Boolean) {
    if (isLoading) {
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
    val scope = rememberCoroutineScope()
    val nestedScrollConnection = rememberToolbarNestedScrollConnection()
    var isMenuVisible by remember { mutableStateOf(false) }
    
    val targetPeekHeight =
        if (menuState.selectedItem != null && isMenuVisible) {
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 48.dp
        } else {
            0.1.dp // Tiny non-zero value to prevent startup expansion issues on some platforms
        }

    val sheetPeekHeight by animateDpAsState(
        targetValue = targetPeekHeight,
        animationSpec = tween(durationMillis = 300),
        label = "sheetPeekHeight"
    )

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        confirmValueChange = { true }
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val hostState = scaffoldState.snackbarHostState
    
    // Force correct initial state and animate in
    LaunchedEffect(menuState.selectedItem) {
        if (menuState.selectedItem != null) {
            // Force partial expansion if it somehow started expanded (common on desktop)
            if (sheetState.currentValue == SheetValue.Expanded) {
                sheetState.partialExpand()
            }
            isMenuVisible = true
        }
    }

    // Stabilized bottom padding for the content inside the scaffold.
    // We use a fixed value based on whether menu is selected, NOT the animated peek height.
    // This prevents the grid from jumping during show/hide animations which triggers Paging3 reload loops.
    val navBarsPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val staticBottomPadding = remember(menuState.selectedItem, navBarsPadding) {
        if (menuState.selectedItem != null) {
            navBarsPadding + 48.dp
        } else {
            0.dp
        }
    }

    val message = stringResource(Res.string.loading_failed)
    val retryText = stringResource(Res.string.retry)

    LaunchedEffect(event.value) {
        val e = event.value ?: return@LaunchedEffect
        log.v("on new event: $event")
        when (e) {
            is UiEvent.Toast -> {
                // Will be handled by platform layer
            }
            is UiEvent.Retry -> {
                val result = hostState.showSnackbar(message = message, actionLabel = retryText)
                when (result) {
                    SnackbarResult.Dismissed -> {}
                    SnackbarResult.ActionPerformed -> onRetry()
                }
            }
            else -> {}
        }
    }
    

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetDragHandle = null,
        sheetContent = {
            BottomMenu(
                scaffoldState.bottomSheetState,
                menuState,
                onMenuItemSelected = {
                    onMenuItemSelected(it)
                    scope.launch { sheetState.partialExpand() }
                },
            )
        },
        sheetPeekHeight = sheetPeekHeight,
        sheetMaxWidth = 640.dp, // Explicit max width for desktop
        sheetContainerColor = Color.Transparent,
        sheetContentColor = MaterialTheme.colorScheme.onPrimary,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        snackbarHost = { SnackbarHost(hostState) }
    ) { innerPadding ->
        // Only apply top padding to the Box, allowing it to extend behind the bottom sheet
        Box(
            Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .nestedScroll(nestedScrollConnection)
        ) {
            LazyGrid(
                GridState(
                    state = gridState,
                    bottomPadding = staticBottomPadding,
                    nestedScrollConnection = nestedScrollConnection,
                    photos = photos,
                    selectedPhotoIndex = selectedPhotoIndex,
                    preloadingPhotoId = preloadingPhotoId,
                    onPhotoClicked = onPhotoClicked,
                    onFirstVisibleItem = onFirstVisibleItem,
                    onScrollingUp = { isScrollingUp ->
                        // Instruction 5: Disable scroll visibility toggling while the bottom sheet is moving or expanded.
                        if (sheetState.currentValue == SheetValue.PartiallyExpanded && !sheetState.isAnimationRunning) {
                            isMenuVisible = isScrollingUp
                        }
                    }
                )
            )
            MainToolbar(
                state = toolbarState,
                modifier =
                Modifier.offset {
                    IntOffset(
                        x = 0,
                        y =
                        nestedScrollConnection.toolbarOffsetHeightPx.floatValue
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
        state.WatchScrollDirection { isScrollingUp ->
            log.v("scroll direction: $isScrollingUp")
            onScrollingUp(isScrollingUp)
        }

        val firstItemState = remember { mutableStateOf<Photo?>(null) }
        LaunchedEffect(state.firstVisibleItemIndex) {
            val index = state.firstVisibleItemIndex
            if (photos.itemCount > index) {
                firstItemState.value = photos[index]
            }
        }
        onFirstVisibleItem(firstItemState)

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
            modifier = Modifier.fillMaxSize(),
            contentPadding =
            PaddingValues(
                top = nestedScrollConnection.toolbarHeight,
                bottom = bottomPadding
            ),
        ) {
            pagedItems(photos) { item ->
                val isPreloading = item.id == preloadingPhotoId
                Thumb(item, onPhotoClicked, isPreloading)
            }
        }
    }

@Composable
private fun LazyStaggeredGridState.WatchScrollDirection(
    onDirectionChanged: (Boolean) -> Unit
) {
    LaunchedEffect(this) {
        var previousIndex = firstVisibleItemIndex
        var previousScrollOffset = firstVisibleItemScrollOffset
        var previousDirection: Boolean? = null
        val thresholdPx = 10 // Ignore tiny layout shifts

        snapshotFlow { firstVisibleItemIndex to firstVisibleItemScrollOffset }
            .collect { (currIndex, currOffset) ->
                if (currIndex == previousIndex && abs(currOffset - previousScrollOffset) < thresholdPx) return@collect

                val isScrollingUp = if (currIndex != previousIndex) {
                    currIndex < previousIndex
                } else {
                    currOffset < previousScrollOffset
                }

                if (isScrollingUp != previousDirection) {
                    onDirectionChanged(isScrollingUp)
                    previousDirection = isScrollingUp
                }

                previousIndex = currIndex
                previousScrollOffset = currOffset
            }
    }
}
