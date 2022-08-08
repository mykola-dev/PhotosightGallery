package ds.photosight.compose.ui.screen.viewer

import android.content.ClipData.Item
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.screen.navigation.MainViewModel
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.util.log
import ds.photosight.compose.util.logCompositions

@Destination
@Composable
fun ViewerScreen(/*navigator: DestinationsNavigator,*/ mainViewModel: MainViewModel) {
    logCompositions(msg = "viewer screen")
    val viewModel: ViewerViewModel = hiltViewModel()
    val photos = mainViewModel.photosPagedFlow.collectAsLazyPagingItems()
    val currentPage = mainViewModel.selectedPhoto ?: 0
    log.v("recomposed")
    log.v("size=${photos.itemCount}")
    if (photos.itemCount > 0) { // some bug with paging lib
        ViewerContent(photos, currentPage, mainViewModel::onPhotoSelected)
    }
}

@Composable
fun ViewerContent(
    photos: LazyPagingItems<Photo>,
    currentPage: Int,
    onPageChanged: (Int) -> Unit,
) {

    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { ViewerToolbar() },
        bottomBar = {},
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {

            }
        },
        drawerContent = { Drawer() }
    ) { _ ->
        val pagerState = rememberPagerState()
        LaunchedEffect(pagerState) {
            pagerState.scrollToPage(currentPage)
            log.v("page $currentPage")
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
                    .background(
                        Color.hsv(
                            (0..360)
                                .random()
                                .toFloat(), 1f, 1f
                        )
                    )
                    .fillMaxSize()
            ) {
                ZoomableImage(photo = item)
            }
        }
    }
}

@Composable
fun ViewerToolbar() {
    TopAppBar(
        title = { Text("sample") },
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
        modifier = Modifier.fillMaxSize()
    )
}