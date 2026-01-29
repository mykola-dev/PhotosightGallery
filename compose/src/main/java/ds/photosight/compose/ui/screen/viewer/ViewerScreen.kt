package ds.photosight.compose.ui.screen.viewer

// Note: System UI controller now handled by Activity's enableEdgeToEdge() configuration
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ds.photosight.compose.core.SaveImage
import ds.photosight.compose.ui.events.UiEvent
import ds.photosight.compose.ui.getOrNull
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.screen.MainViewModel
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.ui.theme.TranslucentTheme
import ds.photosight.compose.util.log
import ds.photosight.compose.util.logCompositions
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ViewerScreen(
    mainViewModel: MainViewModel,
    photoId: Int,
    index: Int,
    onBack: () -> Unit,
) {
    logCompositions(msg = "viewer screen")
    val viewModel: ViewerViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val event by viewModel.events.collectAsState(null)
    val photos = mainViewModel.photosPagedFlow.collectAsLazyPagingItems()

    // Set the selected photo when the screen opens
    LaunchedEffect(photoId) {
        mainViewModel.onPhotoSelected(photoId)
    }

    // Use passed index directly for initial page index to ensure stability during transition
    val currentPageIndex = index

    val downloadLauncher = rememberLauncherForActivityResult(SaveImage()) { uri ->
        if (uri != null) {
            viewModel.saveFile(uri)
        }
    }

    if (photos.itemCount > 0) { // some bug with paging lib
        TranslucentTheme {
            ViewerContent(
                state = state,
                event = event,
                photos = photos,
                currentPageIndex = currentPageIndex,
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
                onDownloadClick = { downloadLauncher.launch(viewModel.providePhotoTitle()) },
                onBrowserClick = viewModel::onOpenBrowser,
                onInfoClick = viewModel::onInfo
            )
        }
    }

    // Handle system back button
    BackHandler(enabled = true) {
        onBack()
    }

    // System UI colors are now handled by the Activity's enableEdgeToEdge() configuration
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "LocalContextGetResourceValueCall")
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
    onDrawerToggle: (value: DrawerValue) -> Unit,
    onDownloadClick: () -> Unit,
    onBrowserClick: () -> Unit,
    onInfoClick: () -> Unit
) {

    val scaffoldState = rememberScaffoldState()
    val isFabExpanded = remember { mutableStateOf(false) }
    val infoState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = true)

    val ctx = LocalContext.current
    LaunchedEffect(event) {
        log.v("event=$event")
        when (event) {
            is UiEvent.Snack -> scaffoldState.snackbarHostState.showSnackbar(ctx.getString(event.stringId))
            is UiEvent.OpenInfo -> infoState.show()
            null -> Unit
        }
    }

    with(scaffoldState.drawerState) {
        LaunchedEffect(currentValue) {
            onDrawerToggle(currentValue)
        }
    }

    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            ViewerBottomBar(
                isVisible = state.showUi,
                isExpanded = isFabExpanded,
                onDrawerClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                onDownloadClick = onDownloadClick,
                onBrowserClick = onBrowserClick,
                onInfoClick = onInfoClick,
            )
        },
        floatingActionButton = {
            Fab(state.showUi, isFabExpanded, onShareUrl = onShareUrl, onShareImage = onShareImage)
        },
        isFloatingActionButtonDocked = true,
        drawerContent = { Drawer(state.details) },
        drawerElevation = 0.dp,
        drawerScrimColor = Palette.drawerBackground,
        drawerGesturesEnabled = true,
        drawerShape = RoundedCornerShape(0),
    ) {
        val pagerState = rememberPagerState(initialPage = currentPageIndex, pageCount = { Int.MAX_VALUE / 2 })

        val updatedOnPageChanged by rememberUpdatedState(onPageChanged)
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                updatedOnPageChanged(page)
            }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true
        ) { page ->
            photos
                .getOrNull(page)
                ?.let { item ->
                    ZoomableImage(photo = item, onClicked = {
                        if (isFabExpanded.value) isFabExpanded.value = false
                        else onClicked()
                    })
                }
        }

        ViewerToolbar(state.showUi, state.title, state.subtitle)
    }
    state.currentPhoto?.let { photo ->
        ModalBottomSheetLayout(
            sheetContent = { InfoSheet(photo, infoState.isVisible) },
            sheetState = infoState,
            sheetBackgroundColor = MaterialTheme.colors.primary,
            scrimColor = Palette.translucent
        ) { }
    }
}
