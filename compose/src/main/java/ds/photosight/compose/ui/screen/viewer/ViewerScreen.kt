package ds.photosight.compose.ui.screen.viewer

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.annotation.Destination
import ds.photosight.compose.ui.events.UiEvent
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.screen.navigation.MainViewModel
import ds.photosight.compose.ui.theme.TranslucentTheme
import ds.photosight.compose.util.logCompositions

@Destination
@Composable
fun ViewerScreen(mainViewModel: MainViewModel) {
    logCompositions(msg = "viewer screen")
    val viewModel: ViewerViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val event by viewModel.events.collectAsState(null)
    val photos = mainViewModel.photosPagedFlow.collectAsLazyPagingItems()
    val currentPage = mainViewModel.selected

    if (photos.itemCount > 0) { // some bug with paging lib
        TranslucentTheme {
            ViewerContent(
                state = state,
                event = event,
                photos = photos,
                currentPage = currentPage,
                onPageChanged = {
                    mainViewModel.onPhotoSelected(it)
                    viewModel.onPageChanged(photos[it]!!)
                },
                onClicked = viewModel::onClicked,
                onShareUrl = viewModel::onUrlShare,
                onShareImage = viewModel::onImageShare,
                onDrawerClick = viewModel::onDrawerToggle,
                onDownloadClick = viewModel::onDownload,
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
    currentPage: Int,
    onPageChanged: (Int) -> Unit,
    onClicked: () -> Unit,
    onShareUrl: () -> Unit,
    onShareImage: () -> Unit,
    onDrawerClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onBrowserClick: () -> Unit,
    onInfoClick: () -> Unit
) {

    val scaffoldState = rememberScaffoldState()
    var isFabExtended = remember { mutableStateOf(false) }
    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            ViewerBottomBar(
                isVisible = state.showUi,
                onDrawerClick = onDrawerClick,
                onDownloadClick = onDownloadClick,
                onBrowserClick = onBrowserClick,
                onInfoClick = onInfoClick,
            )
        },
        floatingActionButton = {
            Fab(state.showUi, isFabExtended, onShareUrl = onShareUrl, onShareImage = onShareImage)
        },
        isFloatingActionButtonDocked = !isFabExtended.value,
        drawerContent = { Drawer() },
        drawerGesturesEnabled = true,
    ) {
        val pagerState = rememberPagerState()
        LaunchedEffect(pagerState) {
            pagerState.scrollToPage(currentPage)
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
                    if (isFabExtended.value) isFabExtended.value = false
                    else onClicked()
                })
        }

        ViewerToolbar(state.showUi, state.title, state.subtitle)
    }
}

