@file:Suppress("MoveLambdaOutsideParentheses")

package ds.photosight.compose.ui.screen.gallery

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import ds.photosight.compose.R
import ds.photosight.compose.ui.ToolbarNestedScrollConnection
import ds.photosight.compose.ui.destinations.ViewerScreenDestination
import ds.photosight.compose.ui.dialog.AboutDialog
import ds.photosight.compose.ui.events.UiEvent
import ds.photosight.compose.ui.isolate
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.pagedItems
import ds.photosight.compose.ui.rememberToolbarNestedScrollConnection
import ds.photosight.compose.ui.screen.navigation.MainViewModel
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.util.log
import ds.photosight.compose.util.logCompositions
import ds.photosight.compose.util.rememberDerived
import kotlin.math.roundToInt

@RootNavGraph(start = true)
@Destination
@Composable
fun GalleryScreen(navigator: DestinationsNavigator, mainViewModel: MainViewModel) {
    logCompositions(msg = "root")
    val viewModel: GalleryViewModel = hiltViewModel()
    mainViewModel.setMenuStateFlow(viewModel.menuStateFlow)

    val event: State<UiEvent?> = viewModel.events.collectAsState(null)

    val menuState by viewModel.menuStateFlow.collectAsState()
    val galleryState by viewModel.galleryState.collectAsState()
    val selectedPhoto = mainViewModel.selectedPhoto
    log.v("selected photo=$selectedPhoto")

    val photosStream: LazyPagingItems<Photo> = mainViewModel.photosPagedFlow.collectAsLazyPagingItems()
    //val title = viewModel.title.collectAsState("")

    isolate({ photosStream.loadState }) { state ->
        LaunchedEffect(state) {
            viewModel.updateLoadingState(state)
            viewModel.updateErrorState(state)
        }
    }

    GalleryContent(
        photos = photosStream,
        menuState = menuState,
        galleryState = galleryState,
        selectedPhoto = selectedPhoto,
        onMenuItemSelected = { viewModel.onMenuSelected(it) },
        onPhotoClicked = {
            log.v("index=${it.index}")
            mainViewModel.onPhotoSelected(it.index)
            navigator.navigate(ViewerScreenDestination)
        },
        event = event,
        onRetry = photosStream::retry,
        loadingSlot = { LoadingSlot(galleryState.isLoading) },
        onFirstVisibleItem = { state ->
            state.value?.let { viewModel.setFirstVisibleItem(it) }
        },
        onShowAboutDialog = viewModel::onShowAboutDialog,
        onDismissAboutDialog = viewModel::onDismissAboutDialog
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
    galleryState: GalleryState,
    menuState: MenuState,
    selectedPhoto: Int?,
    event: State<UiEvent?>,
    onMenuItemSelected: (MenuItemState) -> Unit,
    onPhotoClicked: (Photo) -> Unit,
    onRetry: () -> Unit,
    loadingSlot: @Composable () -> Unit,
    onFirstVisibleItem: @Composable (State<Photo?>) -> Unit,
    onShowAboutDialog: () -> Unit,
    onDismissAboutDialog: () -> Unit
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

            LazyGrid(
                nestedScrollConnection,
                photos,
                selectedPhoto,
                onPhotoClicked,
                onFirstVisibleItem,
                { scrollingUp -> showMenu = scrollingUp }
            )
            MainToolbar(
                galleryState.title,
                galleryState.subtitle,
                Modifier.offset { IntOffset(x = 0, y = nestedScrollConnection.toolbarOffsetHeightPx.value.roundToInt()) },
                onShowAboutDialog
            )

            loadingSlot()

            if (galleryState.showAboutDialog) {
                AboutDialog(onDismiss = onDismissAboutDialog)
            }

        }
    }
}

@Composable
private fun LazyGrid(
    nestedScrollConnection: ToolbarNestedScrollConnection,
    photos: LazyPagingItems<Photo>,
    selectedPhoto: Int?,
    onPhotoClicked: (Photo) -> Unit,
    onFirstVisibleItem: @Composable (State<Photo?>) -> Unit,
    onScrollingUp: (Boolean) -> Unit,
) {
    logCompositions(msg = "lazy grid")
    val state: LazyListState = rememberLazyListState()

    LaunchedEffect(selectedPhoto) {
        selectedPhoto?.let { state.scrollToItem(it) }
    }
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

    LazyStaggeredGrid(
        state = state,
        contentPadding = PaddingValues(top = nestedScrollConnection.toolbarHeight),
        cells = StaggeredCells.Fixed(2)
    ) {
        pagedItems(photos) { item ->
            Thumb(item, onPhotoClicked)
        }
    }

/*    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(top = nestedScrollConnection.toolbarHeight),
    ) {
        pagedItems(photos, { it }) { item ->
            logCompositions(msg = "paged items ${item.id}")
            Thumb(item, onPhotoClicked)
        }
    }*/

}

@Composable
private fun LazyListState.isScrollingUp(): State<Boolean> {
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


/*@SuppressLint("UnrememberedMutableState")
@Preview(showSystemUi = true)
@Composable
fun GalleryPreview() {
    PhotosightTheme {
        GalleryContent(
            flowOf(PagingData.empty<Photo>()).collectAsLazyPagingItems(),
            mutableStateOf("World"),
            "world",
            mutableStateOf(false),
            MenuState(),
            0,
            {},
            {},
            null,
            {},
            { },
            {}
        )
    }
}*/
