@file:Suppress("MoveLambdaOutsideParentheses")

package ds.photosight.compose.ui.screen.gallery

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import ds.photosight.compose.R
import ds.photosight.compose.ui.ToolbarNestedScrollConnection
import ds.photosight.compose.ui.dialog.AboutDialog
import ds.photosight.compose.ui.isolate
import ds.photosight.compose.ui.model.MenuItemState
import ds.photosight.compose.ui.model.MenuState
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.pagedItems
import ds.photosight.compose.ui.rememberToolbarNestedScrollConnection
import ds.photosight.compose.ui.screen.navigation.MainViewModel
import ds.photosight.compose.ui.theme.PhotosightTheme
import ds.photosight.compose.ui.widget.LazyStaggeredGrid2
import ds.photosight.compose.ui.widget.StaggeredVerticalGrid
import ds.photosight.compose.util.log
import ds.photosight.compose.util.logCompositions
import ds.photosight.compose.util.rememberDerived
import kotlinx.coroutines.flow.flowOf
import kotlin.math.roundToInt

@RootNavGraph(start = true)
@Destination
@Composable
fun GalleryScreen(mainViewModel: MainViewModel) {
    logCompositions(msg = "root")
    val viewModel: GalleryViewModel = hiltViewModel()
    mainViewModel.setMenuStateFlow(viewModel.menuStateFlow)

    val menuState by viewModel.menuStateFlow.collectAsState()
    val snackbarEvent by viewModel.retryEvent.collectAsState(null)

    val photosStream: LazyPagingItems<Photo> = mainViewModel.photosPagedFlow.collectAsLazyPagingItems()
    val title = viewModel.title.collectAsState("")

    val resources = LocalContext.current.resources
    val subtitle by remember(viewModel.firstVisibleItem) {
        derivedStateOf {
            viewModel.firstVisibleItem.value?.paginationKey?.let { key ->
                if ("/" in key) key
                else resources.getString(R.string.page_, key)
            }
        }
    }

    //produceState(initialValue = , key1 = , key2 = , key3 = , producer = )()

/*    val loadState = snapshotFlow { photosStream.loadState }
        .onEach { }
        .collectAsState(initial = null)*/

    /*val state = produceState<CombinedLoadStates?>(null) {
        value = photosStream.loadState
    }*/

    isolate({ photosStream.loadState }) { state ->
        LaunchedEffect(state) {
            viewModel.updateLoadingState(state)
            viewModel.updateErrorState(state)
        }
    }

    GalleryContent(
        photos = photosStream,
        toolbarTitle = title,
        toolbarSubtitle = subtitle,
        showAboutDialog = viewModel.showAboutDialog,
        menuState = menuState,
        onMenuItemSelected = { viewModel.onMenuSelected(it) },
        onPhotoClicked = { viewModel.onPhotoClicked(it) },
        snackbarEvent = snackbarEvent,
        onRetry = { photosStream.retry() },
        loadingSlot = { LoadingSlot(viewModel.isLoading.value) },
        onFirstVisibleItem = { viewModel.setFirstVisibleItem(it) }
    )
}


@Composable
fun LoadingSlot(
    isLoading: Boolean,
) {
    if (isLoading) {
        logCompositions(msg = "loading")
        LinearProgressIndicator(color = MaterialTheme.colors.secondary, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun GalleryContent(
    photos: LazyPagingItems<Photo>,
    toolbarTitle: State<String>,
    toolbarSubtitle: String? = null,
    showAboutDialog: MutableState<Boolean>,
    menuState: MenuState,
    onMenuItemSelected: (MenuItemState) -> Unit,
    onPhotoClicked: (Photo) -> Unit,
    snackbarEvent: RetryEvent?,
    onRetry: () -> Unit,
    loadingSlot: @Composable () -> Unit,
    onFirstVisibleItem: (Photo) -> Unit,
) {
    logCompositions(msg = "gallery content")

    val nestedScrollConnection = rememberToolbarNestedScrollConnection()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val snackbarState = scaffoldState.snackbarHostState

    val message = stringResource(id = R.string.loading_failed)
    val retryText = stringResource(id = R.string.retry)
    snackbarEvent?.let { event ->
        LaunchedEffect(snackbarState, event) {
            log.v("new event $event")
            val result = scaffoldState.snackbarHostState.showSnackbar(
                message = message,
                actionLabel = retryText
            )
            when (result) {
                SnackbarResult.Dismissed -> {}
                SnackbarResult.ActionPerformed -> onRetry()
            }
        }
    }

    var showMenu by remember {
        mutableStateOf(true)
    }
    val shitPeekHeight = if (showMenu) WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 48.dp
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
                onPhotoClicked,
                onFirstVisibleItem,
                { scrollingUp -> showMenu = scrollingUp }
            )
            MainToolbar(
                toolbarTitle,
                toolbarSubtitle,
                Modifier.offset { IntOffset(x = 0, y = nestedScrollConnection.toolbarOffsetHeightPx.value.roundToInt()) },
                { showAboutDialog.value = true }
            )

            loadingSlot()

            if (showAboutDialog.value) {
                AboutDialog(onDismiss = {
                    showAboutDialog.value = false
                })
            }

        }
    }
}

@Composable
private fun LazyGrid(
    nestedScrollConnection: ToolbarNestedScrollConnection,
    photos: LazyPagingItems<Photo>,
    onPhotoClicked: (Photo) -> Unit,
    onFirstVisibleItem: (Photo) -> Unit,
    onScrollingUp: (Boolean) -> Unit,
) {
    logCompositions(msg = "lazy grid")
    val state: LazyListState = rememberLazyListState()

    val scrollingUp by state.isScrollingUp()
    LaunchedEffect(scrollingUp) {
        log.v("scroll direction: $scrollingUp")
        onScrollingUp(scrollingUp)
    }

    val firstItem by rememberDerived(state) {
        state.firstVisibleItemIndex.let {
            if (photos.itemCount > it) photos[it] else null
        }
    }

    LaunchedEffect(firstItem) {
        firstItem?.let(onFirstVisibleItem)
    }

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


@SuppressLint("UnrememberedMutableState")
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
            {},
            {},
            null,
            {},
            { },
            {}
        )
    }
}
