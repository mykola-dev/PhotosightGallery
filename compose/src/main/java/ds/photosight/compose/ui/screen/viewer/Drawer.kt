package ds.photosight.compose.ui.screen.viewer

import android.text.format.DateUtils
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import ds.photosight.compose.R
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.ui.theme.PhotosightTheme
import ds.photosight.compose.ui.widget.Ratings
import ds.photosight.compose.util.isPreview
import ds.photosight.compose.util.roundToPx
import ds.photosight.parser.PhotoDetails
import java.time.Instant

@Composable
fun ColumnScope.Drawer(state: DetailsState) {
    Crossfade(state) { state ->
        when (state) {
            is DetailsState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            is DetailsState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Image(Icons.Default.Close, null)
            }
            is DetailsState.Payload -> {
                LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues()) {
                    item {
                        Ratings(state.details.stats, state.details.awards)
                    }
                    item {
                        Spacer(Modifier.height(8.dp))
                    }
                    items(state.details.comments) { comment ->
                        Comment(comment)
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
            is DetailsState.Hidden -> {}
        }
    }
}

private val condensedTypeface = Font(DeviceFontFamilyName("sans-serif-condensed"))

@Composable
fun Comment(comment: PhotoDetails.Comment) {
    val dateText = DateUtils.getRelativeDateTimeString(LocalContext.current, comment.timestamp.toEpochMilli(), 0, DateUtils.DAY_IN_MILLIS, 0).toString()
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)) {
        val (author, avatar, text, date, likes) = createRefs()
        Text(
            text = comment.author,
            modifier = Modifier.constrainAs(author) {
                linkTo(avatar.end, likes.start, 16.dp, 8.dp, bias = 0f)
            },
            color = Palette.aotValue,
            fontSize = 18.sp,
            fontFamily = FontFamily(condensedTypeface),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            text = dateText,
            modifier = Modifier.constrainAs(date) {
                top.linkTo(author.bottom)
                start.linkTo(author.start)
            },
            color = Palette.commentTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light
        )
        Text(
            text = comment.text,
            modifier = Modifier.constrainAs(text) {
                top.linkTo(avatar.bottom, 8.dp)
            },
            color = Palette.aotValue,
            fontSize = 14.sp

        )
        Avatar(comment.avatar, Modifier.constrainAs(avatar) {})
        if (comment.likes > 0) {
            Text(
                text = "+${comment.likes}",
                modifier = Modifier.constrainAs(likes) {
                    end.linkTo(parent.end)
                },
                color = Palette.commentTextSecondary,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
fun Avatar(url: String, modifier: Modifier = Modifier, size: Dp = 60.dp) = AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .crossfade(true)
        .placeholder(R.drawable.anonymous)
        .error(R.drawable.anonymous)
        .size(size.roundToPx())
        .build(),
    contentDescription = null,
    contentScale = ContentScale.Crop,
    modifier = modifier
        .clip(CircleShape)
        .size(size)

).also {
    if (isPreview) {
        LocalContext.current.imageLoader.run {
            diskCache?.clear()
            memoryCache?.clear()
        }
    }
}

@Preview
@Composable
fun CommentsPreview() {
    val loremIpsum =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    val comments = remember {
        List(10) {
            PhotoDetails.Comment(
                text = loremIpsum,
                timestamp = Instant.now(),
                author = "Arnold Schwarzenegger",
                avatar = "https://www.looper.com/img/gallery/20-epic-movies-like-avatar-you-need-to-watch-next/l-intro-1645555067.jpg",
                likes = 99,
                isAuthor = false
            )
        }
    }
    PhotosightTheme {
        Surface {
            Comments(comments)
        }
    }
}

@Composable
private fun Comments(comments: List<PhotoDetails.Comment>) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(comments) { comment ->
            Comment(comment)
            Spacer(Modifier.height(32.dp))
        }
    }
}