package ds.photosight.compose.ui.screen.gallery

import android.graphics.Bitmap
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.skydoves.landscapist.glide.GlideImage
import ds.photosight.compose.R
import ds.photosight.compose.ui.model.Photo
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.util.logCompositions
import ds.photosight.parser.PhotoInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Composable
fun Thumb(
    item: Photo,
    onPhotoClicked: (Photo) -> Unit,
) {
    val url = item.thumb
    logCompositions(msg = "paged item ${item.id}")
/*    GlideImage(
        imageModel = url,
        loading = {
            val infiniteTransition = rememberInfiniteTransition()
            val color by infiniteTransition.animateColor(
                initialValue = Palette.greyDark,
                targetValue = Palette.grey,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .drawBehind {
                        drawRect(color)
                    }
            )
        },
        failure = {
            Image(painterResource(id = R.drawable.photo_placeholder), null)
        },
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp)
            .clickable { onPhotoClicked(item) }
            .animateContentSize()
    )*/

    SubcomposeAsyncImage(
        //model = R.drawable.shrek,
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .size(200) // Set the target size to load the image at.
            .crossfade(500)
            .build(),
        loading = {
            val infiniteTransition = rememberInfiniteTransition()
            val color by infiniteTransition.animateColor(
                initialValue = Palette.greyDark,
                targetValue = Palette.grey,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .drawBehind {
                        drawRect(color)
                    }
            )
        },
        error = {
            Image(painterResource(id = R.drawable.photo_placeholder), null)
        },
        contentDescription = item.title,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp)
            .clickable { onPhotoClicked(item) }
            .animateContentSize()
    )

    /* Image(
         painter = painterResource(id = R.drawable.shrek),
         contentDescription = null,
         contentScale = ContentScale.FillWidth,
         modifier = Modifier
             .fillMaxWidth()
             .padding(1.dp)
             .clickable { onPhotoClicked(item) }
             .animateContentSize()
     )*/
}