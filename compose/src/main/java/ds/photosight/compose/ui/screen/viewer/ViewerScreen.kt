package ds.photosight.compose.ui.screen.viewer

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import ds.photosight.compose.ui.theme.Palette
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
    val isFabExpanded = remember { mutableStateOf(false) }

    with(scaffoldState.drawerState) {
        LaunchedEffect(event) {
            when (event) {
                is UiEvent.OpenDrawer ->
                    if (isClosed) {
                        open()
                    }
                else -> {}
            }
        }
        // this is for manual opening
        LaunchedEffect(currentValue) {
            if (currentValue == DrawerValue.Open) {
                onDrawerClick()
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            ViewerBottomBar(
                isVisible = state.showUi,
                isExpanded = isFabExpanded,
                onDrawerClick = onDrawerClick,
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
        drawerScrimColor = Palette.translucent,
        drawerGesturesEnabled = true,
        drawerShape = RoundedCornerShape(0)
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
                    if (isFabExpanded.value) isFabExpanded.value = false
                    else onClicked()
                })
        }

        ViewerToolbar(state.showUi, state.title, state.subtitle)
    }
}

