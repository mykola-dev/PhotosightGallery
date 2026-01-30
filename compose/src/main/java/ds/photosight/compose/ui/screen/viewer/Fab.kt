package ds.photosight.compose.ui.screen.viewer

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.ui.theme.PhotosightTheme

@Composable
fun Fab(
        isVisible: Boolean,
        isExpanded: MutableState<Boolean>,
        onShareUrl: () -> Unit,
        onShareImage: () -> Unit
) {

    BackHandler(isExpanded.value) { isExpanded.value = false }

    AnimatedVisibility(isVisible, enter = scaleIn(), exit = scaleOut()) {
        val updater = updateTransition(isExpanded.value, "updater")
        val cornerAnimated by
                updater.animateInt(label = "corner") { expanded -> if (expanded) 10 else 50 }

        Surface(
                color = MaterialTheme.colorScheme.secondary,
                tonalElevation = 16.dp,
                modifier = Modifier.clip(RoundedCornerShape(percent = cornerAnimated))
        ) {
            updater.AnimatedContent(
                    transitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform() }
            ) { expanded ->
                if (!expanded) {
                    Box(
                            modifier =
                                    Modifier.defaultMinSize(minWidth = 56.dp, minHeight = 56.dp)
                                            .clickable { isExpanded.value = true },
                            contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Share, null) }
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
            text = { Text(title, color = Palette.greyDark) },
            onClick = onClick,
            leadingIcon = { Icon(icon, null, tint = Palette.greyDark) },
            modifier = Modifier.width(IntrinsicSize.Max).defaultMinSize(minWidth = 150.dp)
    )
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
        ) { Box(Modifier.padding(it)) }
    }
}
