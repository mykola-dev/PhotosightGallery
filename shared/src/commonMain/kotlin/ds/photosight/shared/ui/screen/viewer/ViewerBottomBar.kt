package ds.photosight.shared.ui.screen.viewer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun ViewerBottomBar(
    isVisible: Boolean,
    isExpanded: State<Boolean>,
    onDrawerClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onBrowserClick: () -> Unit,
    onInfoClick: () -> Unit,
    fab: @Composable () -> Unit
) {
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val expansionAnim by animateFloatAsState(if (isExpanded.value) 1f else 0f, label = "expansion")

    val notchWidth = 72.dp
    val notchCornerRadius = 50.dp

    val targetDepth = if (isExpanded.value) 0f else 36f
    val notchDepth by animateFloatAsState(targetValue = targetDepth, label = "notchDepth")

    val targetFabY = if (isExpanded.value) -80f else -36f
    val animatedFabY by animateFloatAsState(targetValue = targetFabY, label = "fabY")

    val barShape = remember(notchDepth) { NotchShape(notchWidth, notchDepth.dp, notchCornerRadius) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = barShape
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = bottomPadding)
                        .height(64.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RippleIconButton(onClick = onDrawerClick) {
                        Icon(Icons.Default.Menu, null, tint = Color.White)
                    }
                    RippleIconButton(onClick = onDownloadClick) {
                        Icon(Icons.Default.Save, null, tint = Color.White)
                    }

                    val spacerWidth = (72 * (1f - expansionAnim)).dp
                    Spacer(Modifier.width(spacerWidth))

                    RippleIconButton(onClick = onBrowserClick) {
                        Icon(Icons.Filled.Language, null, tint = Color.White)
                    }
                    RippleIconButton(onClick = onInfoClick) {
                        Icon(Icons.Default.Info, null, tint = Color.White)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(0, (-bottomPadding + animatedFabY.dp).roundToPx()) }
                .align(Alignment.BottomCenter)
        ) {
            fab()
        }
    }
}

@Composable
private fun RippleIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) {
            Color.White.copy(alpha = 0.25f)
        } else {
            Color.Transparent
        },
        label = "rippleHighlight"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null // TODO: Add ripple effect in KMP-compatible way
            ),
        contentAlignment = Alignment.Center
    ) { content() }
}

class NotchShape(private val width: Dp, private val depth: Dp, private val cornerRadius: Dp) :
    Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = with(density) { width.toPx() }
            val d = with(density) { depth.toPx() }
            val cr = with(density) { cornerRadius.toPx() }
            val cx = size.width / 2

            addRect(Rect(0f, 0f, size.width, size.height))

            val notchPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = cx - w / 2,
                        top = -d,
                        right = cx + w / 2,
                        bottom = d,
                        cornerRadius = CornerRadius(cr, cr)
                    )
                )
            }

            op(this, notchPath, PathOperation.Difference)
        }
        return Outline.Generic(path)
    }
}
