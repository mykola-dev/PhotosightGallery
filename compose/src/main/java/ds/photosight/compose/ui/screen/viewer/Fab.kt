package ds.photosight.compose.ui.screen.viewer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ds.photosight.compose.R

@Composable
fun Fab(isVisible: Boolean, isExpanded:MutableState<Boolean>, onShareUrl: () -> Unit, onShareImage: () -> Unit) {
    //var isExpanded by remember { mutableStateOf(false) }

    BackHandler(isExpanded.value) {
        isExpanded.value = false
    }

    AnimatedVisibility(isVisible, enter = scaleIn(), exit = scaleOut()) {
        val updater = updateTransition(isExpanded.value, "updater")
        val cornerAnimated by updater.animateInt(label = "corner") { expanded ->
            if (expanded) 10 else 50
        }

        Surface(
            color = MaterialTheme.colors.secondary,
            elevation = 16.dp,
            modifier = Modifier
                //.offset { IntOffset(0, offsetAnimated) }
                .clip(RoundedCornerShape(percent = cornerAnimated))
        ) {
            updater.AnimatedContent(
                transitionSpec = { fadeIn() with fadeOut() using SizeTransform() }
            ) { expanded ->
                if (!expanded) {
                    Box(
                        modifier = Modifier
                            .defaultMinSize(minWidth = 56.dp, minHeight = 56.dp)
                            .clickable {
                                isExpanded.value = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Share, null, modifier = Modifier
                            //.padding(8.dp)
                        )
                    }
                } else {
                    Column {
                        MenuItem(Icons.Default.Link, stringResource(R.string.share_link), onShareUrl)
                        MenuItem(Icons.Default.Image, stringResource(R.string.share_img), onShareImage)
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    DropdownMenuItem(
        onClick = onClick,
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .defaultMinSize(minWidth = 200.dp)
    ) {
        Icon(icon, null)
        Spacer(Modifier.width(8.dp))
        Text(title)
    }
}

/*
@Preview
@Composable
fun FabPreview() {
    PhotosightTheme {
        Fab(
            true,
            onShareUrl = {},
            onShareImage = {}
        )
    }
}*/
