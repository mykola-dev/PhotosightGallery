package ds.photosight.shared.ui.screen.viewer

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ds.photosight.shared.ui.theme.Palette
import ds.photosight.shared.core.widget.Ratings
import ds.photosight.parser.PhotoDetails

@Composable
fun ColumnScope.Drawer(state: DetailsState) {
    Crossfade(state) { state ->
        when (state) {
            is DetailsState.Loading ->
                    Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            is DetailsState.Error ->
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Image(Icons.Default.Close, null)
                    }
            is DetailsState.Payload -> {
                LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues()) {
                    item { Ratings(state.details.stats, state.details.awards) }
                    item { Spacer(Modifier.height(8.dp)) }
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

@Composable
fun Comment(comment: PhotoDetails.Comment) {
    // Simplified date formatting - using epoch timestamp
    val dateText = comment.timestamp.toString().take(10)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Avatar(comment.avatar, size = 48.dp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = comment.author,
                        color = Palette.aotValue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        text = dateText,
                        color = Palette.commentTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
            if (comment.likes > 0) {
                Text(
                    text = "+${comment.likes}",
                    color = Palette.commentTextSecondary,
                    fontSize = 12.sp,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = comment.text,
            color = Palette.aotValue,
            fontSize = 14.sp
        )
    }
}

@Composable
fun Avatar(url: String, modifier: Modifier = Modifier, size: Dp = 60.dp) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(CircleShape).size(size)
    )
}
