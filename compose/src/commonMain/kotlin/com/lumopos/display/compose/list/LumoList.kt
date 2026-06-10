package com.lumopos.display.compose.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp


/**
 * A composable function for displaying a title for a list or list section.
 *
 * This component renders a `Text` element with specific styling suitable for a list title,
 * including `titleMedium` typography, `onSurfaceVariant` color, and standard horizontal
 * and vertical padding. It is typically used within list composable like `CdsList` and `CdsLazyList`.
 *
 * @param title The string to be displayed as the title.
 */
@Composable
fun LumoListTitle(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    )
}

/**
 * A composable function for displaying a title for a `CdsListGroup`.
 *
 * This component renders a `Text` element with styling appropriate for a list group title,
 * including `titleMedium` typography, `onSurfaceVariant` color, and vertical padding.
 * Unlike `CdsListTitle`, this version does not include horizontal padding, making it
 * suitable for titles that are aligned with the content of a `CdsListGroup` rather than
 * the full width of the screen.
 *
 * @param title The string to be displayed as the title.
 */
@Composable
fun LumoListGroupTitle(title: String) {
    LumoListTitle(title)
}

/**
 * A Composable function that displays a single list item.
 * It can include a headline, leading and trailing icons or text,
 * and optional overline and supporting text.
 * The item can be clickable.
 *
 * @param headline The main text of the list item.
 * @param shape The shape of the list item's surface. Defaults to [RectangleShape].
 * @param color The background color of the list item. Defaults to [Color.Transparent].
 * @param contentColor The preferred color for content displayed on top of the [color].
 *                     Defaults to the content color appropriate for the [color].
 * @param verticalAlignment The vertical alignment of the content within the list item.
 *                          Defaults to [Alignment.CenterVertically].
 * @param leading An optional Composable to be displayed at the start of the list item.
 * @param overline An optional string to be displayed above the headline.
 * @param supporting An optional string to be displayed below the headline.
 * @param trailing An optional Composable to be displayed at the end of the list item.
 * @param onClick An optional lambda to be executed when the list item is clicked.
 *                If null, the item will not be clickable.
 */
@Composable
fun LumoListItem(
    headline: String,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    color: Color = Color.Transparent,
    contentColor: Color = contentColorFor(color),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 12.dp
    ),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leading: @Composable () -> Unit = {},
    overline: String? = null,
    supporting: String? = null,
    trailing: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    if (enabled && onClick != null) {
        Surface(
            modifier = modifier,
            shape = shape,
            color = color,
            contentColor = contentColor,
            onClick = onClick
        ) {
            LumoListItemContent(
                headline = headline,
                verticalAlignment = verticalAlignment,
                contentPadding = contentPadding,
                leading = leading,
                overline = overline,
                supporting = supporting,
                trailing = trailing
            )
        }
    } else {
        Surface(
            shape = shape,
            color = color,
            contentColor = contentColor,
            modifier = if (enabled) modifier else modifier.alpha(0.38f)
        ) {
            LumoListItemContent(
                headline = headline,
                verticalAlignment = verticalAlignment,
                contentPadding = contentPadding,
                leading = leading,
                overline = overline,
                supporting = supporting,
                trailing = trailing
            )
        }
    }
}

/**
 * A composable function that represents an individual item within a `CdsListGroup`.
 *
 * This component is essentially a specialized version of `CdsListItem`, styled for use
 * within a group. It supports various elements like a headline, leading/trailing icons or text,
 * overline text, and supporting text. It can also be made clickable and visually indicate
 * an active state.
 *
 * The background color of the item changes based on its `isActive` state and whether an
 * `onClick` handler is provided.
 *
 * @param headline The main text content of the list item.
 * @param verticalAlignment The vertical alignment of the content within the item. Defaults to `Alignment.CenterVertically`.
 * @param leading An optional composable lambda to display content at the beginning of the item (e.g., an icon).
 * @param overline An optional string to display above the headline.
 * @param supporting An optional string to display below the headline, providing additional context.
 * @param trailing An optional composable lambda to display content at the end of the item (e.g., a switch or additional text).
 * @param onClick An optional lambda to be executed when the item is clicked. If null, the item will not be clickable.
 * @param isActive A boolean indicating whether the item is currently in an active state. This affects the background color. Defaults to `false`.
 */
@Composable
fun LumoListGroupItem(
    headline: String,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 12.dp
    ),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leading: @Composable () -> Unit = {},
    overline: String? = null,
    supporting: String? = null,
    trailing: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isActive: Boolean = false
) {
    LumoListItem(
        modifier = modifier,
        shape = shape,
        color = if (!isActive) MaterialTheme.colorScheme.surfaceContainerLowest else MaterialTheme.colorScheme.secondaryContainer,
        headline = headline,
        contentPadding = contentPadding,
        verticalAlignment = verticalAlignment,
        leading = leading,
        overline = overline,
        supporting = supporting,
        trailing = trailing,
        onClick = onClick,
        enabled = enabled
    )
}

object LumoList {
    fun getGroupedShape(
        itemsSize: Int,
        index: Int,
        reverseLayout: Boolean = false
    ): Shape {
        return if (itemsSize == 1) {
            singleGroupShape()
        } else {
            when (index) {
                0 -> {
                    if (reverseLayout) {
                        bottomListGroupShape()
                    } else {
                        topListGroupShape()
                    }
                }
                itemsSize - 1 -> {
                    if (reverseLayout) {
                        topListGroupShape()
                    } else {
                        bottomListGroupShape()
                    }
                }
                else -> {
                    middleListGroupShape()
                }
            }
        }
    }

    fun singleGroupShape() : Shape = RoundedCornerShape(28.dp)

    fun topListGroupShape() : RoundedCornerShape = RoundedCornerShape(
        topStart = 28.dp,
        topEnd = 28.dp,
        bottomStart = 8.dp,
        bottomEnd = 8.dp
    )

    fun bottomListGroupShape() : RoundedCornerShape = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 8.dp,
        bottomStart = 28.dp,
        bottomEnd = 28.dp
    )

    fun middleListGroupShape() : RoundedCornerShape = RoundedCornerShape(8.dp)
}


/**
 * A private Composable function that defines the content structure of a CdsListItem.
 * It arranges the headline, optional leading/trailing elements, overline, and supporting text
 * in a Row layout.
 *
 * This function is responsible for the internal layout and styling of the list item's content.
 * It dynamically adjusts padding based on the presence of overline and supporting text.
 * It also applies specific text styles and content colors to different parts of the item.
 *
 * @param headline The main text of the list item.
 * @param verticalAlignment The vertical alignment of the content within the row.
 *                          Defaults to [Alignment.CenterVertically].
 * @param leading An optional Composable lambda to be displayed at the start of the item.
 *                Its content color is set to `onSurfaceVariant`.
 * @param overline An optional string to be displayed above the headline.
 *                 Styled as `labelMedium` with `onSurfaceVariant` color.
 * @param supporting An optional string to be displayed below the headline.
 *                   Styled as `bodyMedium` with `onSurfaceVariant` color.
 * @param trailing An optional Composable lambda to be displayed at the end of the item.
 *                 Its content color is set to `onSurfaceVariant` and text style is merged
 *                 with `labelSmall`.
 */
@Composable
private fun LumoListItemContent(
    headline: String,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 12.dp
    ),
    leading: @Composable (() -> Unit)? = null,
    overline: String? = null,
    supporting: String? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = if (overline != null && supporting != null) Modifier.heightIn(min = 88.dp)
            .padding(
                contentPadding
            )
        else if (overline != null || supporting != null)
            Modifier.heightIn(min = 72.dp)
                .padding(contentPadding)
        else
            Modifier.heightIn(min = 56.dp)
                .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = verticalAlignment
    ) {
        val decoratedLeading: @Composable (() -> Unit)? =
            leading?.let {
                {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                        content = it,
                    )
                }
            }
        if (decoratedLeading != null) {
            decoratedLeading()
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            if (overline != null) {
                Text(
                    text = overline,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = headline,
                style = MaterialTheme.typography.bodyLarge
            )
            if (supporting != null) {
                Text(
                    text = supporting,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        val decoratedTrailing: @Composable (() -> Unit)? =
            trailing?.let {
                {
                    val mergedStyle =
                        LocalTextStyle.current.merge(MaterialTheme.typography.labelSmall)
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                        LocalTextStyle provides mergedStyle,
                        content = it,
                    )
                }
            }

        if (decoratedTrailing != null) {
            decoratedTrailing()
        }
    }
}