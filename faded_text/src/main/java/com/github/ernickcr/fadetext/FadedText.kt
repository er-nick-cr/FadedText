package com.github.ernickcr.fadetext

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.github.ernickcr.fadetext.util.splitAtIndex

/*
 * Designed and developed by 2025 er-nick-cr (Eremeev Nikita)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@Composable
fun FadedText(
    text: String,
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
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    fadeWidth: Dp = 10.dp
) {
    val textMeasurer = rememberTextMeasurer()
    val textColor = color.takeOrElse { style.color.takeOrElse { LocalContentColor.current } }
    var width by remember { mutableIntStateOf(0) }
    val fadeWidthPx = with(LocalDensity.current) { fadeWidth.toPx() }
    val fadeStop = remember(width) { 1 - (fadeWidthPx / width) }
    val gradient = Brush.horizontalGradient(
        colorStops = arrayOf(
            0.0F to textColor,
            fadeStop / 2 to textColor,
            fadeStop to textColor.copy(alpha = 0.0F),
        ),
        tileMode = TileMode.Clamp,
    )
    val localStyle = TextStyle(
        color = textColor,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = textAlign ?: TextAlign.Start,
        lineHeight = lineHeight,
        fontFamily = fontFamily,
        textDecoration = textDecoration,
        fontStyle = fontStyle,
        letterSpacing = letterSpacing,
    )
    val lastLineLocalStyle = TextStyle(
        brush = gradient,
        fontSize = fontSize,
        fontWeight = fontWeight,
        lineBreak = LineBreak.Simple,
        textAlign = textAlign ?: TextAlign.Start,
        lineHeight = lineHeight,
        fontFamily = fontFamily,
        textDecoration = textDecoration,
        fontStyle = fontStyle,
        letterSpacing = letterSpacing,
    )

    val textLayoutResult = remember(width) {
        textMeasurer.measure(
            text = text,
            style = style.merge(localStyle),
            constraints = Constraints.fixedWidth(width),
        )
    }
    val shouldFade = remember(width) {
        textLayoutResult.lineCount > maxLines && width > 0
    }

    Box(
        modifier = Modifier
            .then(modifier)
            .onSizeChanged { width = it.width }
    ) {
        if (shouldFade && maxLines > 1) {
            val lastLineStartIndex = textLayoutResult.getLineStart(maxLines - 1)
            val (firstLines, lastLines) = text.splitAtIndex(lastLineStartIndex)

            Column(modifier = Modifier.fillMaxWidth()) {
                BasicText(
                    text = firstLines,
                    style = style.merge(localStyle),
                    minLines = minLines,
                    modifier = modifier,
                )

                Text(
                    text = lastLines,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = style.merge(lastLineLocalStyle),
                    minLines = minLines,
                )
            }
        } else {
            BasicText(
                text = text,
                style = if (shouldFade) style.merge(lastLineLocalStyle) else style.merge(localStyle),
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                softWrap = if (shouldFade) false else true,
                minLines = minLines,
            )
        }
    }
}