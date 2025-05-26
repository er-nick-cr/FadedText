package com.github.ernickcr.fadedtext

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.Paragraph
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
import com.github.ernickcr.fadedtext.util.splitAtIndex

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

/**
 * Provides text with configurable smooth fading effect on the last line
 *
 * The default [style] uses the [LocalTextStyle] provided by the [MaterialTheme] / components. If
 * you are setting your own style, you may want to consider first retrieving [LocalTextStyle], and
 * using [TextStyle.copy] to keep any theme defined attributes, only modifying the specific
 * attributes you want to override.
 *
 * For ease of use, commonly used parameters from [TextStyle] are also present here. The order of
 * precedence is as follows:
 * - If a parameter is explicitly set here (i.e, it is _not_ `null` or [TextUnit.Unspecified]), then
 *   this parameter will always be used.
 * - If a parameter is _not_ set, (`null` or [TextUnit.Unspecified]), then the corresponding value
 *   from [style] will be used instead.
 *
 * Additionally, for [color], if [color] is not set, and [style] does not have a color, then
 * [LocalContentColor] will be used.
 *
 * @param text the text to be displayed
 * @param modifier the [Modifier] to be applied to this layout node
 * @param color [Color] to apply to the text. If [Color.Unspecified], and [style] has no color set,
 *   this will be [LocalContentColor].
 * @param fontSize the size of glyphs to use when painting the text. See [TextStyle.fontSize].
 * @param fontStyle the typeface variant to use when drawing the letters (e.g., italic). See
 *   [TextStyle.fontStyle].
 * @param fontWeight the typeface thickness to use when painting the text (e.g., [FontWeight.Bold]).
 * @param fontFamily the font family to be used when rendering the text. See [TextStyle.fontFamily].
 * @param letterSpacing the amount of space to add between each letter. See
 *   [TextStyle.letterSpacing].
 * @param textDecoration the decorations to paint on the text (e.g., an underline). See
 *   [TextStyle.textDecoration].
 * @param textAlign the alignment of the text within the lines of the paragraph. See
 *   [TextStyle.textAlign].
 * @param lineHeight line height for the [Paragraph] in [TextUnit] unit, e.g. SP or EM. See
 *   [TextStyle.lineHeight].
 * @param maxLines An optional maximum number of lines for the text to span, wrapping if necessary.
 *   If the text exceeds the given number of lines, it will be truncated according to [overflow] and
 *   [softWrap]. It is required that 1 <= [minLines] <= [maxLines].
 * @param minLines The minimum height in terms of minimum number of visible lines. It is required
 *   that 1 <= [minLines] <= [maxLines].
 * @param style style configuration for the text such as color, font, line height etc.
 * @param fadeWidth width of the last line's fade
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

                BasicText(
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
                softWrap = !shouldFade,
                minLines = minLines,
            )
        }
    }
}