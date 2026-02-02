package ds.photosight.shared.ui.screen.viewer

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
// BackHandler from androidx.compose.ui works across all platforms
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ds.photosight.shared.ui.getOrNull
import ds.photosight.shared.ui.model.Photo
import ds.photosight.shared.ui.events.UiEvent
import ds.photosight.shared.ui.modifiers.LocalAnimatedVisibilityScope
import ds.photosight.shared.ui.modifiers.LocalSharedTransitionScope
import ds.photosight.shared.ui.screen.MainViewModel
import ds.photosight.shared.ui.theme.Palette
import ds.photosight.shared.ui.theme.TranslucentTheme
import ds.photosight.shared.util.log
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource
import photosight.shared.generated.resources.Res
import photosight.shared.generated.resources.saved_successfully

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ViewerScreen(
    mainViewModel: MainViewModel,
    photoId: Int,
    index: Int,
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<ViewerViewModel>()
    val state by viewModel.state.collectAsState()
    val event by viewModel.events.collectAsState(null)
    val photos = mainViewModel.photosPagedFlow.collectAsLazyPagingItems()

    // Access shared transition scopes if needed for animations
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    // Debug logging
    log.d("ViewerScreen: photoId=$photoId, index=$index, photos.itemCount=${photos.itemCount}")
    log.d("ViewerScreen: photos.loadState=${photos.loadState}")
    
    LaunchedEffect(photoId) { 
        log.d("ViewerScreen: onPhotoSelected called for photoId=$photoId")
        mainViewModel.onPhotoSelected(photoId) 
    }

    if (photos.itemCount > 0) {
        TranslucentTheme {
            ViewerContent(
                state = state,
                event = event,
                photos = photos,
                currentPageIndex = index,
                onPageChanged = {
                    photos.getOrNull(it)?.let { photo ->
                        mainViewModel.onPhotoSelected(photo.id)
                        viewModel.onPageChanged(photo)
                    }
                },
                onClicked = { viewModel.onClicked() },
                onShareUrl = viewModel::onUrlShare,
                onShareImage = viewModel::onImageShare,
                onDrawerToggle = viewModel::onDrawerStateChanged,
                onDownloadClick = { viewModel.saveFile() },
                onBrowserClick = viewModel::onOpenBrowser,
                onInfoClick = viewModel::onInfo
            )
        }
    } else {
        // Show loading indicator while photos are loading
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Palette.surface),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.CircularProgressIndicator()
        }
    }

    BackHandler(enabled = true) { onBack() }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ViewerContent(
    state: ViewerState,
    event: UiEvent?,
    photos: LazyPagingItems<Photo>,
    currentPageIndex: Int,
    onPageChanged: (Int) -> Unit,
    onClicked: () -> Unit,
    onShareUrl: () -> Unit,
    onShareImage: () -> Unit,
    onDrawerToggle: (value: Boolean) -> Unit,
    onDownloadClick: () -> Unit,
    onBrowserClick: () -> Unit,
    onInfoClick: () -> Unit
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val isFabExpanded = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showSheet by remember { mutableStateOf(false) }

    val savedSuccessMessage = stringResource(Res.string.saved_successfully)
    LaunchedEffect(event) {
        log.v("event=$event")
        when (event) {
            is UiEvent.Snack -> snackbarHostState.showSnackbar(savedSuccessMessage)
            is UiEvent.OpenInfo -> showSheet = true
            null -> Unit
        }
    }

    LaunchedEffect(drawerState.currentValue) { onDrawerToggle(drawerState.isOpen) }

    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = Palette.drawerBackground,
                drawerShape = RoundedCornerShape(0),
                drawerTonalElevation = 0.dp
            ) {
                Drawer(state.details)
            }
        },
        gesturesEnabled = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Palette.surface)
        ) {
            // Use actual item count, with minimum of 1 to avoid empty pager
            val actualPageCount = maxOf(photos.itemCount, 1)
            log.d("ViewerContent: actualPageCount=$actualPageCount, currentPageIndex=$currentPageIndex")
            
            val pagerState =
                rememberPagerState(
                    initialPage = currentPageIndex.coerceIn(0, actualPageCount - 1),
                    pageCount = { actualPageCount }
                )

            val updatedOnPageChanged by rememberUpdatedState(onPageChanged)
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }.collect { page ->
                    log.d("HorizontalPager: currentPage=$page, itemCount=${photos.itemCount}")
                    updatedOnPageChanged(page)
                    
                    // Trigger loading more items when approaching the end
                    if (page >= photos.itemCount - 3 && photos.loadState.append.endOfPaginationReached.not()) {
                        log.d("Triggering load for next page...")
                        photos.loadState.append.let { 
                            // Try to trigger loading by accessing an item near the end
                            photos.getOrNull(photos.itemCount - 1)
                        }
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = true,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                log.d("HorizontalPager content: page=$page, itemCount=${photos.itemCount}")
                val item = photos.getOrNull(page)
                log.d("HorizontalPager content: item at page $page = ${item?.id ?: "NULL"}")
                if (item != null) {
                    log.d("Rendering ZoomableImage for photo ${item.id}, url=${item.large}")
                    ZoomableImage(
                        photo = item,
                        onClicked = {
                            if (isFabExpanded.value) isFabExpanded.value = false
                            else onClicked()
                        }
                    )
                } else {
                    // Show loading placeholder when photo is not loaded yet
                    log.d("Showing loading placeholder for page $page")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            Box(Modifier.align(Alignment.TopCenter)) {
                ViewerToolbar(state.showUi, state.title, state.subtitle)
            }

            Box(Modifier.align(Alignment.BottomCenter)) {
                ViewerBottomBar(
                    isVisible = state.showUi,
                    isExpanded = isFabExpanded,
                    onDrawerClick = { scope.launch { drawerState.open() } },
                    onDownloadClick = onDownloadClick,
                    onBrowserClick = onBrowserClick,
                    onInfoClick = onInfoClick,
                    fab = {
                        Fab(
                            state.showUi,
                            isFabExpanded,
                            onShareUrl = onShareUrl,
                            onShareImage = onShareImage
                        )
                    }
                )
            }

            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            ) {
                SnackbarHost(snackbarHostState)
            }
        }
    }

    if (showSheet && state.currentPhoto != null) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.primary,
            scrimColor = Palette.translucent
        ) { InfoSheet(state.currentPhoto, true) }
    }
}
