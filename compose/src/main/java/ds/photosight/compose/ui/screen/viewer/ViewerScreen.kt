package ds.photosight.compose.ui.screen.viewer

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.annotation.Destination
import ds.photosight.compose.core.SaveImage
import ds.photosight.compose.repo.getIndexById
import ds.photosight.compose.ui.events.UiEvent
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.screen.navigation.MainViewModel
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.ui.theme.TranslucentTheme
import ds.photosight.compose.util.log
import ds.photosight.compose.util.logCompositions
import kotlinx.coroutines.launch

@Destination
@Composable
fun ViewerScreen(mainViewModel: MainViewModel) {
    logCompositions(msg = "viewer screen")
    val viewModel: ViewerViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val event by viewModel.events.collectAsState(null)
    val photos = mainViewModel.photosPagedFlow.collectAsLazyPagingItems()
    val currentPageIndex = photos.getIndexById(mainViewModel.selectedId) ?: 0

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
                    val photo = photos[it]!!
                    mainViewModel.onPhotoSelected(photo.id)
                    viewModel.onPageChanged(photo)
                },
                onClicked = viewModel::onClicked,
                onShareUrl = viewModel::onUrlShare,
                onShareImage = viewModel::onImageShare,
                onDrawerToggle = viewModel::onDrawerStateChanged,
                onDownloadClick = { downloadLauncher.launch(viewModel.providePhotoTitle()) },
                onBrowserClick = viewModel::onOpenBrowser,
                onInfoClick = viewModel::onInfo
            )
        }
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(color = Color(0x01000000))    // transparent doesn't work on nav bar :(
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
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
    val infoState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val ctx = LocalContext.current
    LaunchedEffect(event) {
        log.v("event=$event")
        when (event) {
            is UiEvent.Snack -> scaffoldState.snackbarHostState.showSnackbar(ctx.getString(event.stringId))
            is UiEvent.OpenInfo -> infoState.show()
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
        drawerShape = RoundedCornerShape(0)
    ) {
        val pagerState = rememberPagerState()
        LaunchedEffect(pagerState) {
            pagerState.scrollToPage(currentPageIndex)
        }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }
                .collect { page ->
                    onPageChanged(page)
                }
        }

        HorizontalPager(
            count = Int.MAX_VALUE / 2,
            state = pagerState,
        ) { index ->
            val item = photos[index] ?: error("empty item")
            ZoomableImage(
                photo = item,
                onClicked = {
                    if (isFabExpanded.value) isFabExpanded.value = false
                    else onClicked()
                })
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



