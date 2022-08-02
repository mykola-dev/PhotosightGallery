@file:Suppress("MoveLambdaOutsideParentheses")

package ds.photosight.compose.ui.screen.gallery

import android.graphics.drawable.AnimationDrawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import ds.photosight.compose.R
import ds.photosight.compose.ui.ToolbarNestedScrollConnection
import ds.photosight.compose.ui.dialog.AboutDialog
import ds.photosight.compose.ui.model.MenuItemState
import ds.photosight.compose.ui.model.MenuState
import ds.photosight.compose.ui.model.ScrollDirection
import ds.photosight.compose.ui.pagedItems
import ds.photosight.compose.ui.rememberToolbarNestedScrollConnection
import ds.photosight.compose.ui.screen.navigation.MainViewModel
import ds.photosight.compose.util.log
import ds.photosight.parser.PhotoInfo
import kotlin.math.roundToInt

@RootNavGraph(start = true)
@Destination
@Composable
fun GalleryScreen(mainViewModel: MainViewModel) {
    val viewModel: GalleryViewModel = hiltViewModel()
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

    val title by derivedStateOf {
        menuState.selectedItem?.title ?: viewModel.appName
    }
    val resources = LocalContext.current.resources
    val subtitle by derivedStateOf {
        viewModel.firstVisibleItem.value?.paginationKey?.let { key ->
            if ("/" in key) key
            else resources.getString(R.string.page_, key)
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
    toolbarTitle: String,
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

    val state: LazyListState = rememberLazyListState()

    //state.

    val firstItem by derivedStateOf {
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
            val url = item!!.thumb
            AsyncImage(
                model = getPreviewCoilModel(url),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp)
                    .defaultMinSize(100.dp)
                    .clickable { onPhotoClicked(item) }
            )
        }
    }
}


@Composable
private fun getPreviewCoilModel(url: String) = ImageRequest.Builder(LocalContext.current)
    .data(url)
    .error(R.drawable.ic_photo_error)
    .placeholder((ContextCompat.getDrawable(LocalContext.current, R.drawable.photo_placeholder) as AnimationDrawable).apply {
        setExitFadeDuration(1000)
        start()
    })
    .crossfade(true)
    .build()

/*
@Preview(showSystemUi = true)
@Composable
fun GalleryPreview() {
    PhotosightTheme {
        GalleryContent("Hello", "World", mutableStateOf(false))
    }
}*/
