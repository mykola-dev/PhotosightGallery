package ds.photosight.compose.ui.screen.viewer

import android.annotation.SuppressLint
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ds.photosight.compose.R
import ds.photosight.compose.ui.theme.PhotosightTheme

@Composable
fun Fab(isVisible: Boolean, isExpanded: MutableState<Boolean>, onShareUrl: () -> Unit, onShareImage: () -> Unit) {

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
                .clip(RoundedCornerShape(percent = cornerAnimated))
        ) {
           /* val sizeTransform = SizeTransform { initialSize, targetSize ->
                keyframes {
                    if (initialSize.width < targetSize.width) {
                        IntSize(initialSize.width, targetSize.height) at 100
                    } else {
                        IntSize(targetSize.width, initialSize.height) at 100
                    }
                    this.durationMillis = 300
                }
            }*/
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
                        Icon(Icons.Default.Share, null)
                    }
                } else {
                    Column {
                        MenuItem(Icons.Default.Link, stringResource(R.string.share_link)) {
                            onShareUrl()
                            isExpanded.value = false
                        }
                        MenuItem(Icons.Default.Image, stringResource(R.string.share_img)) {
                            onShareImage()
                            isExpanded.value = false
                        }
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

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun FabPreview() {
    PhotosightTheme {
        Scaffold(
            floatingActionButton = {
                Fab(
                    true,
                    onShareUrl = {},
                    onShareImage = {},
                    isExpanded = mutableStateOf(false)
                )
            },
            bottomBar = { BottomAppBar {} },
            isFloatingActionButtonDocked = true,
        ) {
            it
        }

    }
}
