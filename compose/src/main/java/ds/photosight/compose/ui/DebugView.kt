package ds.photosight.compose.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toDrawable
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import ds.photosight.compose.util.logCompositions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

object DebugVM {
    val state = flow {
        generateSequence(0) { it + 1 }
            .forEach {
                delay(10)
                emit(it)
            }
    }
}

@Composable
fun DebugView() {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) {
        ctx.imageLoader.diskCache?.clear()
        ctx.imageLoader.memoryCache?.clear()
    }
    val thumb = "https://cdn77-pic.xnxx-cdn.com/videos/thumbs169lll/d6/86/2f/d6862f3667e6eeeec1d468364b47ab59/d6862f3667e6eeeec1d468364b47ab59.17.jpg"
    val url = "https://media.tits-guru.com/images/3fbca286-7f16-46cb-afab-8782a1384174.jpeg"
    val placeholderKey = "key"
    var showSecondImage by remember { mutableStateOf(false) }
    Column {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(thumb)
                .placeholderMemoryCacheKey(placeholderKey)
                .build(),
            contentDescription = null,
            modifier = Modifier.clickable {
                Log.v("#", "click")
                showSecondImage = !showSecondImage
            }
        )

        if (showSecondImage) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .placeholderMemoryCacheKey(placeholderKey)
                    .build(),
                contentDescription = null,
            ) {
                val cacheKey = this.painter.request.placeholderMemoryCacheKey!!
                Log.v("key", cacheKey.key)
                val placeholder = painter
                    .imageLoader
                    .memoryCache
                    ?.get(MemoryCache.Key(placeholderKey))  // should be cached but it's null
                    ?.bitmap
                    ?.asImageBitmap()
                Log.v("placeholder", "placeholder=$placeholder")

                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        if (placeholder != null)
                            Image(placeholder, null)   // should show placeholder for a while but it's empty
                        CircularProgressIndicator()
                    }
                    is AsyncImagePainter.State.Success -> {
                        this.SubcomposeAsyncImageContent()
                    }
                    else -> {}
                }
            }
        }
    }

}

class Data(
    var string: String
)

//@Stable
data class Data2(
    val string: String
)

@Composable
fun Widget(data: Data) {
    logCompositions("widget")

    Text(text = data.string, color = Color.White)
}
