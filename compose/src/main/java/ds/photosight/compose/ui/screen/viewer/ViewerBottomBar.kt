package ds.photosight.compose.ui.screen.viewer

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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
        val fabOffset by animateFloatAsState(if (isVisible) 0f else 1f, label = "fabOffset")
        val expansionAnim by
                animateFloatAsState(if (isExpanded.value) 1f else 0f, label = "expansion")

        val barShape = RectangleShape

        // The entire bottom area
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                // Bottom Bar Background & Content
                AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it }),
                ) {
                        Box(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .background(
                                                        color = MaterialTheme.colorScheme.surface,
                                                        shape = barShape
                                                )
                        ) {
                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(bottom = bottomPadding)
                                                        .height(64.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        // Left Group
                                        RippleIconButton(onClick = onDrawerClick) {
                                                Icon(Icons.Default.Menu, null, tint = Color.White)
                                        }
                                        RippleIconButton(onClick = onDownloadClick) {
                                                Icon(Icons.Default.Save, null, tint = Color.White)
                                        }

                                        // Spacer for FAB (Width includes side gaps)
                                        // At expansion = 1, width is 144dp (120dp menu + 2*12dp
                                        // gaps)
                                        val spacerWidth = (72 + (144 - 72) * expansionAnim).dp
                                        Spacer(Modifier.width(spacerWidth))

                                        // Right Group
                                        RippleIconButton(onClick = onBrowserClick) {
                                                Icon(
                                                        Icons.Default.Language,
                                                        null,
                                                        tint = Color.White
                                                )
                                        }
                                        RippleIconButton(onClick = onInfoClick) {
                                                Icon(Icons.Default.Info, null, tint = Color.White)
                                        }
                                }
                        }
                }

                // FAB (Custom Position) - Floating above the notch
                Box(
                        modifier =
                                Modifier.offset(y = 100.dp * fabOffset) // Slide out with bar
                                        .offset(
                                                y = -bottomPadding - 32.dp
                                        ) // Adjusted for 56dp FAB + notch
                                        .align(Alignment.BottomCenter)
                ) { fab() }
        }
}

@Composable
private fun RippleIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val backgroundColor by
                animateColorAsState(
                        targetValue =
                                if (isPressed) Color.White.copy(alpha = 0.25f)
                                else Color.Transparent,
                        label = "rippleHighlight"
                )

        Box(
                modifier =
                        Modifier.size(48.dp)
                                .clip(CircleShape)
                                .background(backgroundColor)
                                .clickable(
                                        onClick = onClick,
                                        interactionSource = interactionSource,
                                        indication = ripple(color = Color.White, bounded = true)
                                ),
                contentAlignment = Alignment.Center
        ) { content() }
}
