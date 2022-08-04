package ds.photosight.compose.ui.screen.gallery

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import ds.photosight.compose.R
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.parser.PhotoInfo

@Composable
fun Thumb(
    item: PhotoInfo,
    onPhotoClicked: (PhotoInfo) -> Unit,
) {
    val url = item.thumb
    SubcomposeAsyncImage(
        model = url,
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
            //.defaultMinSize(100.dp)
            .clickable { onPhotoClicked(item) }
            .animateContentSize()
    )
}