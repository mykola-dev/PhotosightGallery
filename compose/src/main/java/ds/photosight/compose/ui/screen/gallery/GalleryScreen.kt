@file:Suppress("MoveLambdaOutsideParentheses")

package ds.photosight.compose.ui.screen.gallery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import ds.photosight.compose.R
import ds.photosight.compose.ui.ToolbarNestedScrollConnection
import ds.photosight.compose.ui.dialog.AboutDialog
import ds.photosight.compose.ui.model.MenuItemState
import ds.photosight.compose.ui.model.MenuState
import ds.photosight.compose.ui.pagedItems
import ds.photosight.compose.ui.rememberToolbarNestedScrollConnection
import ds.photosight.compose.ui.screen.navigation.MainViewModel
import ds.photosight.compose.util.log
import ds.photosight.compose.util.logCompositions
import ds.photosight.compose.util.rememberDerived
import ds.photosight.parser.PhotoInfo
import kotlin.math.roundToInt

@RootNavGraph(start = true)
@Destination
@Composable
fun GalleryScreen(mainViewModel: MainViewModel) {
    val viewModel: GalleryViewModel = viewModel()
    mainViewModel.setMenuStateFlow(viewModel.menuStateFlow)

    val menuState by viewModel.menuStateFlow.collectAsState()
    val snackbarEvent by viewModel.retryEvent.collectAsState(null)

    val photosStream = mainViewModel.photosPagedFlow.collectAsLazyPagingItems()

    LaunchedEffect(photosStream.loadState) {
        photosStream.loadState.let { state ->
            viewModel.updateLoadingState(state)
            viewModel.updateErrorState(state)
        }
    }

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
        isLoading = viewModel.isLoading.value,
        onFirstVisibleItem = { viewModel.setFirstVisibleItem(it) }
    )
}

@Composable
fun GalleryContent(
    photos: LazyPagingItems<PhotoInfo>,
    toolbarTitle: State<String>,
    toolbarSubtitle: String? = null,
    showAboutDialog: MutableState<Boolean>,
    menuState: MenuState,
    onMenuItemSelected: (MenuItemState) -> Unit,
    onPhotoClicked: (PhotoInfo) -> Unit,
    snackbarEvent: RetryEvent?,
    onRetry: () -> Unit,
    isLoading: Boolean,
    onFirstVisibleItem: (PhotoInfo) -> Unit,
) {
    val nestedScrollConnection = rememberToolbarNestedScrollConnection()
    val shitPeekHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 48.dp
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

    BottomSheetScaffold(
        sheetContent = {
            BottomMenu(
                scaffoldState.bottomSheetState,
                menuState,
                onMenuItemSelected
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
                onFirstVisibleItem
            )
            MainToolbar(
                toolbarTitle,
                toolbarSubtitle,
                Modifier.offset { IntOffset(x = 0, y = nestedScrollConnection.toolbarOffsetHeightPx.value.roundToInt()) },
                { showAboutDialog.value = true }
            )

            if (isLoading) {
                LinearProgressIndicator(color = MaterialTheme.colors.secondary, modifier = Modifier.fillMaxWidth())
            }

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
    photos: LazyPagingItems<PhotoInfo>,
    onPhotoClicked: (PhotoInfo) -> Unit,
    onFirstVisibleItem: (PhotoInfo) -> Unit,
    //onDirection:(ScrollDirection)->Unit,
) {
    logCompositions(msg = "lazy grid")
    val state: LazyListState = rememberLazyListState()

   /* val scrollingDown = remember { mutableStateOf(false) }


    val scrollingDown = produceState(initialValue = false) {
        var previousIndex = state.firstVisibleItemScrollOffset
        snapshotFlow { state.firstVisibleItemScrollOffset }
            .collect { index ->
                value = index < previousIndex
                previousIndex = index
            }
    }*/

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
}


/*
@Preview(showSystemUi = true)
@Composable
fun GalleryPreview() {
    PhotosightTheme {
        GalleryContent("Hello", "World", mutableStateOf(false))
    }
}*/
