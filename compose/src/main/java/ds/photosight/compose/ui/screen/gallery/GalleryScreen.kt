package ds.photosight.compose.ui.screen.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells
import ds.photosight.compose.R

@Composable
fun GalleryScreen(viewModel: GalleryViewModel = viewModel()) {
    GalleryContent()
}

@Composable
fun GalleryContent() {
    TopAppBar(
        title = {
            Text("Photosight Gallery")
        }
    )

    val urls = remember {
        listOf(
            "https://cdny.de/p/x/b/798/7251492.jpg"
        )
    }

    LazyStaggeredGrid(cells = StaggeredCells.Adaptive(minSize = 180.dp)) {
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
}

@Composable
private fun getPreviewCoilModel(url: String) = ImageRequest.Builder(LocalContext.current)
    .data(url)
    .crossfade(true)
    .build()

@Preview
@Composable
fun GalleryPreview() {
    GalleryContent()
}