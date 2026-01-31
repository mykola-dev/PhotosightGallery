package ds.photosight.compose.core.widget

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ds.photosight.compose.R
import ds.photosight.compose.data.asDrawableResource
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.ui.theme.PhotosightTheme
import ds.photosight.parser.PhotoDetails

private val PhotoDetails.Stats.ratingFactor: Float
    get() = if (likes + dislikes > 0) likes.toFloat() / (likes + dislikes) else 0f

@Composable
fun Ratings(stats: PhotoDetails.Stats, awardsList: List<PhotoDetails.Award>) {
    // Using stringResource() instead of LocalContext.current.getString() for proper invalidation on config changes
    val labelsList = listOf(
        stringResource(R.string.views),
        stringResource(R.string.rating_artistic),
        stringResource(R.string.rating_original),
        stringResource(R.string.rating_technic),
        stringResource(R.string.likes),
        stringResource(R.string.dislikes),
    )
    val statList = remember { with(stats) { listOf(views, art, original, tech, likes, dislikes) } }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(Palette.translucentGrey)
            .statusBarsPadding()
    ) {
        val (labels, bar, values, awards) = createRefs()

        var started by remember { mutableStateOf(false) }
        SideEffect {
            started = true
        }

        Column(
            horizontalAlignment = Alignment.End,
            modifier =
                Modifier.constrainAs(labels) {
                    start.linkTo(parent.start, 32.dp)
                    linkTo(parent.top, parent.bottom, 32.dp, 32.dp, bias = 0f)
                }
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

        val animatedOffset by
        animateFloatAsState(
            if (started) stats.ratingFactor else 0f,
            tween(1000, easing = EaseInOut)
        )
        Canvas(
            Modifier.constrainAs(bar) {
                linkTo(labels.top, labels.bottom)
                start.linkTo(labels.end, 16.dp)
                width = Dimension.value(2.dp)
                height = Dimension.fillToConstraints
            }
        ) {
            drawRect(Palette.red)
            val hOffset = size.height - size.height * animatedOffset
            drawRect(
                color = Palette.blue,
                topLeft = Offset(0f, hOffset),
            )
        }

        Column(
            Modifier.constrainAs(values) {
                start.linkTo(bar.end, 16.dp)
                top.linkTo(labels.top)
            }
        ) {
            statList.forEach { stat ->
                val statAnimated =
                    animateIntAsState(if (started) stat else 0, tween(1000, easing = EaseOut))
                if (started) Value(statAnimated)
            }
        }

        Column(
            Modifier.constrainAs(awards) {
                end.linkTo(parent.end, 16.dp)
                linkTo(labels.top, parent.bottom, 0.dp, 16.dp, bias = 0f)
            }
        ) {
            awardsList.map { it.asDrawableResource(LocalContext.current) }
                .forEachIndexed { index, icon ->
                    AnimatedVisibility(
                        visible = started,
                        enter = scaleIn(tween(800, index * 50, EaseOut)) +
                            fadeIn(tween(800, index * 100, EaseOut))
                    ) {
                        Image(painterResource(icon), null, Modifier.padding(bottom = 4.dp))
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

@Preview
@Composable
fun RatingsPreview() {
    PhotosightTheme {
        Surface {
            Ratings(PhotoDetails.Stats(10, 20, 30, 40, 50, 60), PhotoDetails.Award.entries.toList())
        }
    }
}
