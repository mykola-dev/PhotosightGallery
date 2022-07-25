@file:Suppress("MoveLambdaOutsideParentheses")

package ds.photosight.compose.ui.screen.gallery

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import ds.photosight.compose.ui.dialog.AboutDialog
import ds.photosight.compose.ui.rememberToolbarNestedScrollConnection
import ds.photosight.compose.ui.theme.PhotosightTheme
import kotlin.math.roundToInt

@RootNavGraph(start = true)
@Destination
@Composable
fun GalleryScreen() {
    val viewModel: GalleryViewModel = hiltViewModel()

    GalleryContent(
        viewModel.appName,
        null,
        viewModel.showAboutDialog,

        )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GalleryContent(
    toolbarTitle: String,
    toolbarSubtitle: String? = null,
    showAboutDialog: MutableState<Boolean>,
) {

    val nestedScrollConnection = rememberToolbarNestedScrollConnection()

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

                AsyncImage(
                    model = getPreviewCoilModel(urls.random()),
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
            Modifier.offset { IntOffset(x = 0, y = nestedScrollConnection.toolbarOffsetHeightPx.value.roundToInt()) },
            toolbarSubtitle,
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

@Composable
private fun MainToolbar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onShowAboutDialog: () -> Unit
) {
    TopAppBar(
        contentPadding = WindowInsets.statusBars.asPaddingValues(),
        modifier = modifier
    ) {
        Row {
            Column(Modifier.padding(horizontal = 16.dp).weight(1f)) {
                Text(title)
                if (subtitle != null) {
                    Text(subtitle, fontSize = 12.sp)
                }
            }
            IconButton(onClick = onShowAboutDialog) {
                Icon(Icons.Filled.Info, null)
            }
        }
    }
}

@Composable
private fun getPreviewCoilModel(url: String) = ImageRequest.Builder(LocalContext.current)
    .data(url)
    .crossfade(true)
    .build()

@Preview(showSystemUi = true)
@Composable
fun GalleryPreview() {
    PhotosightTheme {
        GalleryContent("Hello", "World", mutableStateOf(false))
    }
}