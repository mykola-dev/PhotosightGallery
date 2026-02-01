package ds.photosight.shared.core.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ds.photosight.shared.ui.theme.Palette
import ds.photosight.parser.PhotoDetails

private val PhotoDetails.Stats.ratingFactor: Float
    get() = if (likes + dislikes > 0) likes.toFloat() / (likes + dislikes) else 0f

@Composable
fun Ratings(stats: PhotoDetails.Stats, awardsList: List<PhotoDetails.Award>) {
    val labelsList = listOf(
        "Views",
        "Artistic",
        "Original",
        "Technic",
        "Likes",
        "Dislikes",
    )
    val statList = remember { with(stats) { listOf(views, art, original, tech, likes, dislikes) } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Palette.translucentGrey)
            .statusBarsPadding()
            .padding(32.dp)
    ) {
        var started by remember { mutableStateOf(false) }
        SideEffect {
            started = true
        }

        // Labels and Values Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Labels column
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                labelsList.forEachIndexed { index, label ->
                    AnimatedVisibility(
                        started,
                        enter = fadeIn(tween(800, index * 100, LinearEasing))
                            .plus(slideInHorizontally(tween(800, index * 100, EaseOutBack)) { -100 })
                    ) {
                        Label(label)
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            // Rating Bar
            val animatedOffset by animateFloatAsState(
                if (started) stats.ratingFactor else 0f,
                tween(1000, easing = EaseInOut)
            )
            Canvas(
                Modifier
                    .width(2.dp)
                    .height(180.dp)
            ) {
                drawRect(Palette.red)
                val hOffset = size.height - size.height * animatedOffset
                drawRect(
                    color = Palette.blue,
                    topLeft = Offset(0f, hOffset),
                )
            }

            Spacer(Modifier.width(16.dp))

            // Values column
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                statList.forEach { stat ->
                    val statAnimated =
                        animateIntAsState(if (started) stat else 0, tween(1000, easing = EaseOut))
                    if (started) Value(statAnimated)
                }
            }

            Spacer(Modifier.width(16.dp))

            // Awards column - simplified without Android-specific drawable resource
            Column(
                horizontalAlignment = Alignment.End
            ) {
                awardsList.forEachIndexed { index, award ->
                    AnimatedVisibility(
                        visible = started,
                        enter = scaleIn(tween(800, index * 50, EaseOut)) +
                            fadeIn(tween(800, index * 100, EaseOut))
                    ) {
                        // Simplified award display - just text for now
                        Text(
                            text = award.name.take(1), // First letter as placeholder
                            modifier = Modifier.size(24.dp),
                            color = Palette.aotValue,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Value(value: State<Int>) {
    Text(
        text = value.value.toString(),
        modifier = Modifier,
        color = Palette.aotLabel,
        style =
            MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Light,
                fontSize = 22.sp
            )
    )
}

@Composable
private fun Label(label: String) {
    Text(
        text = label,
        modifier = Modifier,
        color = Palette.aotLabel,
        style =
            MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Thin,
                fontSize = 22.sp,
                textAlign = TextAlign.End
            )
    )
}
