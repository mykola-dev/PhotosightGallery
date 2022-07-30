@file:Suppress("MoveLambdaOutsideParentheses")

package ds.photosight.compose.ui.screen.gallery

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import ds.photosight.compose.ui.dialog.AboutDialog
import ds.photosight.compose.ui.model.MenuItemState
import ds.photosight.compose.ui.model.MenuState
import ds.photosight.compose.ui.rememberToolbarNestedScrollConnection
import kotlin.math.roundToInt

@RootNavGraph(start = true)
@Destination
@Composable
fun GalleryScreen() {
    val viewModel: GalleryViewModel = hiltViewModel()

    val menuState by viewModel.menuStateFlow.collectAsState()
/*    val bottomSheetValue = remember {
        derivedStateOf { if (menuState.value.categories.isEmpty()) BottomSheetValue.Collapsed }
    }*/

    GalleryContent(
        toolbarTitle = "",//viewModel.appName,
        toolbarSubtitle = null,
        showAboutDialog = viewModel.showAboutDialog,
        menuState = menuState,
        onMenuItemSelected = { viewModel.onMenuSelected(it) },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GalleryContent(
    toolbarTitle: String,
    toolbarSubtitle: String? = null,
    showAboutDialog: MutableState<Boolean>,
    menuState: MenuState,
    onMenuItemSelected: (MenuItemState) -> Unit,

    ) {
    val nestedScrollConnection = rememberToolbarNestedScrollConnection()
    val shitPeekHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 48.dp
    val scaffoldState = rememberBottomSheetScaffoldState()

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
        sheetPeekHeight = shitPeekHeight
    ) {

        Box(
            Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {

            val urls = remember {
                listOf(
                    "https://cdny.de/p/x/b/798/7251492.jpg"
                )
            }

            LazyStaggeredGrid(
                contentPadding = PaddingValues(top = nestedScrollConnection.toolbarHeight),
                cells = StaggeredCells.Adaptive(minSize = 180.dp)
            ) {
                items(60) {
                    val random: Double = 100 + Math.random() * (500 - 100)
                    val url=""//urls.random()
                    AsyncImage(
                        model = getPreviewCoilModel(url),
                        contentDescription = null,

                        /*painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier
                            .height(random.dp)
                            .padding(10.dp),
                        contentScale = ContentScale.Crop*/
                    )
                }
            }

            MainToolbar(
                toolbarTitle,
                toolbarSubtitle,
                Modifier.offset { IntOffset(x = 0, y = nestedScrollConnection.toolbarOffsetHeightPx.value.roundToInt()) },
                { showAboutDialog.value = true }
            )

            LinearProgressIndicator(color = MaterialTheme.colors.secondary, modifier = Modifier.fillMaxWidth())

            if (showAboutDialog.value) {
                AboutDialog {
                    showAboutDialog.value = false
                }
            }

        }
    }


}


@Composable
private fun getPreviewCoilModel(url: String) = ImageRequest.Builder(LocalContext.current)
    .data(url)
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
