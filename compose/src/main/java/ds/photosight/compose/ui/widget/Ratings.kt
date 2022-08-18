package ds.photosight.compose.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import ds.photosight.compose.util.log
import ds.photosight.compose.util.logCompositions
import ds.photosight.parser.PhotoDetails

private val PhotoDetails.Stats.ratingFactor: Float get() = 1 / (likes + dislikes).toFloat() * likes.toFloat()

@Composable
fun Ratings(stats: PhotoDetails.Stats, awardsList: List<PhotoDetails.Award>) {
    val ctx = LocalContext.current
    val labelsList = remember {
        listOf(
            R.string.views,
            R.string.rating_artistic,
            R.string.rating_original,
            R.string.rating_technic,
            R.string.likes,
            R.string.dislikes,
        ).map { ctx.getString(it) }
    }
    val statList = remember {
        with(stats) {
            listOf(views, art, original, tech, likes, dislikes)
        }
    }
    logCompositions("ratings")

    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .background(Color(0x20808080))
            .statusBarsPadding()
    ) {
        val (labels, bar, values, awards) = createRefs()

        var started by remember { mutableStateOf(false) }
        SideEffect {
            log.v("side effect")
            started = true
            //animationStarted.targetState = true
        }

        Column(horizontalAlignment = Alignment.End, modifier = Modifier.constrainAs(labels) {
            start.linkTo(parent.start, 32.dp)
            linkTo(parent.top, parent.bottom, 32.dp, 32.dp, bias = 0f)
        }) {
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

        val animatedOffset by animateFloatAsState(
            if (started) stats.ratingFactor else 0f,
            tween(1000, easing = EaseInOut)
        )
        Canvas(
            Modifier
                .constrainAs(bar) {
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

        Column(Modifier.constrainAs(values) {
            start.linkTo(bar.end, 16.dp)
            top.linkTo(labels.top)
        }) {
            statList.forEach { stat ->
                val statAnimated = animateIntAsState(
                    if (started) stat else 0,
                    tween(1000, easing = EaseOut)
                )
                if (started) Value(statAnimated)
            }
        }

        Column(Modifier.constrainAs(awards) {
            end.linkTo(parent.end, 16.dp)
            linkTo(labels.top, parent.bottom, 0.dp, 16.dp, bias = 0f)
        }) {
            awardsList
                .map { it.asDrawableResource(LocalContext.current) }
                .forEachIndexed { index, icon ->
                    AnimatedVisibility(
                        started,
                        enter = scaleIn(tween(800, index * 50, EaseOut)) + fadeIn(tween(800, index * 100, EaseOut))
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
        fontSize = 22.sp,
        fontWeight = FontWeight.Light,
    )
}

@Composable
private fun Label(label: String) {
    Text(
        text = label,
        modifier = Modifier,
        color = Palette.aotLabel,
        fontSize = 22.sp,
        fontWeight = FontWeight.Thin,
        textAlign = TextAlign.End,
    )
}

@Preview
@Composable
fun RatingsPreview() {
    PhotosightTheme {
        Surface {
            Ratings(PhotoDetails.Stats(10, 20, 30, 40, 50, 60), PhotoDetails.Award.values().toList())
        }
    }
}