package ds.photosight.shared.core.widget

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/**
 * Simplified LinkifyText for Compose Multiplatform.
 * This is a basic implementation without Android-specific SpannableString.
 * For full functionality, platform-specific implementations may be needed.
 */
@Composable
fun LinkifyText(
        text: String,
        modifier: Modifier = Modifier,
        linkColor: Color = Color.Blue,
        linkEntire: Boolean = false,
        color: Color = Color.Unspecified,
        fontSize: TextUnit = TextUnit.Unspecified,
        fontStyle: FontStyle? = null,
        fontWeight: FontWeight? = null,
        fontFamily: FontFamily? = null,
        letterSpacing: TextUnit = TextUnit.Unspecified,
        textDecoration: TextDecoration? = null,
        textAlign: TextAlign? = null,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Clip,
        softWrap: Boolean = true,
        maxLines: Int = Int.MAX_VALUE,
        onTextLayout: (TextLayoutResult) -> Unit = {},
        style: TextStyle = LocalTextStyle.current,
        clickable: Boolean = true,
        onClickLink: ((linkText: String) -> Unit)? = null
) {
    val uriHandler = LocalUriHandler.current
    
    // Simplified link detection - just handle URLs starting with http/https
    val linkInfos = remember(text) {
        if (linkEntire) {
            listOf(LinkInfo(text, 0, text.length))
        } else {
            findUrls(text)
        }
    }
    
    val annotatedString = buildAnnotatedString {
        append(text)
        linkInfos.forEach {
            addStyle(
                    style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
                    start = it.start,
                    end = it.end
            )
            addStringAnnotation(tag = "tag", annotation = it.url, start = it.start, end = it.end)
        }
    }
    
    if (clickable) {
        ClickableText(
                text = annotatedString,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign,
                lineHeight = lineHeight,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                onTextLayout = onTextLayout,
                style = style,
                onClick = { offset ->
                    annotatedString
                            .getStringAnnotations(
                                    start = offset,
                                    end = offset,
                            )
                            .firstOrNull()
                            ?.let { result ->
                                if (linkEntire) {
                                    onClickLink?.invoke(
                                            annotatedString.substring(result.start, result.end)
                                    )
                                } else {
                                    uriHandler.openUri(result.item)
                                    onClickLink?.invoke(
                                            annotatedString.substring(result.start, result.end)
                                    )
                                }
                            }
                }
        )
    } else {
        Text(
                text = annotatedString,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign,
                lineHeight = lineHeight,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                onTextLayout = onTextLayout,
                style = style
        )
    }
}

private fun findUrls(text: String): List<LinkInfo> {
    val urls = mutableListOf<LinkInfo>()
    val urlRegex = Regex("(https?://[^\\s]+)")
    urlRegex.findAll(text).forEach { matchResult ->
        urls.add(LinkInfo(matchResult.value, matchResult.range.first, matchResult.range.last + 1))
    }
    return urls
}

private data class LinkInfo(val url: String, val start: Int, val end: Int)

@Composable
private fun ClickableText(
        text: AnnotatedString,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontSize: TextUnit = TextUnit.Unspecified,
        fontStyle: FontStyle? = null,
        fontWeight: FontWeight? = null,
        fontFamily: FontFamily? = null,
        letterSpacing: TextUnit = TextUnit.Unspecified,
        textDecoration: TextDecoration? = null,
        textAlign: TextAlign? = null,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Clip,
        softWrap: Boolean = true,
        maxLines: Int = Int.MAX_VALUE,
        onTextLayout: (TextLayoutResult) -> Unit = {},
        style: TextStyle = LocalTextStyle.current,
        onClick: (Int) -> Unit
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator =
            Modifier.pointerInput(onClick) {
                detectTapGestures { pos ->
                    layoutResult.value?.let { layoutResult ->
                        onClick(layoutResult.getOffsetForPosition(pos))
                    }
                }
            }
    Text(
            text = text,
            modifier = modifier.then(pressIndicator),
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            onTextLayout = {
                layoutResult.value = it
                onTextLayout(it)
            },
            style = style
    )
}
