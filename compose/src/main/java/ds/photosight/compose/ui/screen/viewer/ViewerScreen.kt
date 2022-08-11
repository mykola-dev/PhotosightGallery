package ds.photosight.compose.ui.screen.viewer

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toDrawable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.annotation.Destination
import ds.photosight.compose.ui.events.UiEvent
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.screen.navigation.MainViewModel
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.ui.theme.TranslucentTheme
import ds.photosight.compose.ui.widget.zoomable
import ds.photosight.compose.util.log
import ds.photosight.compose.util.logCompositions

@Destination
@Composable
fun ViewerScreen(mainViewModel: MainViewModel) {
    logCompositions(msg = "viewer screen")
    val viewModel: ViewerViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val event by viewModel.events.collectAsState(null)
    val photos = mainViewModel.photosPagedFlow.collectAsLazyPagingItems()
    val currentPage = mainViewModel.selectedPhoto ?: 0
    if (photos.itemCount > 0) { // some bug with paging lib
        TranslucentTheme {
            ViewerContent(
                state = state,
                event = event,
                photos = photos,
                currentPage = currentPage,
                onPageChanged = mainViewModel::onPhotoSelected
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
) {

    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        //topBar = { ViewerToolbar() },
        bottomBar = {
            if (state.showUi) ViewerBottomBar()
        },
        floatingActionButton = {
            if (state.showUi) {
                FloatingActionButton(onClick = {
                    log.v("share it!")  // todo
                }) {
                    Icon(Icons.Default.Share, null)
                }
            }
        },
        isFloatingActionButtonDocked = true,
        drawerContent = { Drawer() }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ZoomableImage(photo = item)
            }
        }
    }
}

@Composable
fun ViewerBottomBar() {
    BottomAppBar(
        cutoutShape = CircleShape,
        modifier = Modifier.navigationBarsPadding()
    ) {

    }
}

@Composable
fun ViewerToolbar() {
    TopAppBar(
        title = { Text("sample") },
        modifier = Modifier.statusBarsPadding()
    )
}

@Composable
fun Drawer() {
    Column(Modifier.background(Palette.translucent)) {
        Text("the menu")
    }
}

@Composable
fun ZoomableImage(photo: Photo) {
    SubcomposeAsyncImage(
        model = photo.large,
        contentDescription = photo.title,
    ) {
        val state = painter.state

        when (state) {
            is AsyncImagePainter.State.Loading, is AsyncImagePainter.State.Success -> {
                log.v("loading...")
                val cacheKey = MemoryCache.Key(photo.thumb)
                painter
                    .imageLoader
                    .memoryCache
                    ?.get(cacheKey)
                    ?.bitmap
                    ?.asImageBitmap()
                    ?.let { placeholder ->
                        log.v("placeholder=$placeholder")
                        Image(placeholder, contentDescription = null, modifier = Modifier.fillMaxSize())
                    }

            }
            is AsyncImagePainter.State.Error -> {
                log.e("error")
                Image(Icons.Default.Close, null)
            }
            else -> error("not supported state ${painter.state}")
        }
        val showProgress by derivedStateOf { state is AsyncImagePainter.State.Loading }
        val success by derivedStateOf { state is AsyncImagePainter.State.Success }

        AnimatedVisibility(success, enter = fadeIn()) {
            state as AsyncImagePainter.State.Success
            val scale = painter.intrinsicSize.run {
                log.v("w=$width h=$height")
                width / height
            }
            log.v("success. scale=$scale source=${state.result.dataSource} cached=${state.result.isPlaceholderCached}")
            SubcomposeAsyncImageContent(
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(scale)
            )
        }

        AnimatedVisibility(showProgress, exit = fadeOut()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
    }
}
