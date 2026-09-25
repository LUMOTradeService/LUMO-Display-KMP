package com.lumopos.display.compose.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lumopos.display.compose.dialog.state.LumoDialogState

/**
 * A composable function that displays a standard Material Design alert dialog, tailored for the POS application.
 * This is an overload that simplifies state management by accepting a [LumoDialogState] object.
 *
 * It provides a consistent look and feel for simple confirmation or information dialogs
 * across the application. The dialog includes an icon, a title, a text body, a confirm button,
 * and an optional dismiss button. The visibility and actions are controlled via the [state] parameter.
 *
 * Example usage:
 * ```
 * val dialogState = rememberPOSDialogState()
 * // In some composable:
 * POSAlertDialog(
 *     state = dialogState,
 *     icon = { Icon(Icons.Default.Warning, contentDescription = null) },
 *     title = "Confirm Deletion",
 *     text = "Are you sure you want to delete this item? This action cannot be undone.",
 *     confirmButtonText = "Delete",
 *     dismissButtonText = "Cancel"
 * )
 *
 * // To show the dialog:
 * Button(onClick = { dialogState.show() }) {
 *     Text("Delete Item")
 * }
 * ```
 *
 * @param icon A composable lambda for the icon to be displayed at the top of the dialog.
 * @param title The title of the dialog, displayed below the icon.
 * @param text The main body content of the dialog.
 * @param confirmButtonText The text to display on the confirmation button.
 * @param dismissButtonText The text to display on the dismiss button. If null, the dismiss button will not be shown.
 * @param state The [LumoDialogState] that controls the visibility and callbacks (`onConfirmRequest`, `onDismissRequest`) for the dialog.
 */
@Composable
fun LumoAlertDialog(
    icon: @Composable () -> Unit,
    title: String,
    text: String,
    confirmButtonText: String,
    dismissButtonText: String?,
    state: LumoDialogState,
) {
    when {
        state.isVisible -> {
            AlertDialog(
                icon = icon,
                title = {
                    Text(title)
                },
                text = {
                    Text(text)
                },
                onDismissRequest = state::dismiss,
                confirmButton = {
                    TextButton(
                        onClick = state::confirm
                    ) {
                        Text(confirmButtonText)
                    }
                },
                dismissButton = dismissButtonText?.let {
                    {
                        TextButton(
                            onClick = state::dismiss
                        ) {
                            Text(dismissButtonText)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun LumoDialog(
    icon: @Composable () -> Unit,
    title: String,
    confirmButtonText: String,
    dismissButtonText: String?,
    state: LumoDialogState,
    modifier: Modifier = Modifier,
    confirmButtonEnabled: Boolean = true,
    dismissButtonEnabled: Boolean = true,
    text: String? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp),
    content: @Composable (ColumnScope.() -> Unit)
) {
    LumoDialog(
        icon = icon,
        title = title,
        modifier = modifier,
        text = text,
        confirmButtonEnabled = confirmButtonEnabled,
        confirmButtonText = confirmButtonText,
        dismissButtonEnabled = dismissButtonEnabled,
        dismissButtonText = dismissButtonText,
        onConfirmRequest = state::confirm,
        onDismissRequest = state::dismiss,
        isVisible = state.isVisible,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
private fun LumoDialog(
    icon: @Composable () -> Unit,
    title: String,
    confirmButtonText: String,
    dismissButtonText: String?,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButtonEnabled: Boolean = true,
    dismissButtonEnabled: Boolean = true,
    text: String? = null,
    isVisible: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp),
    content: @Composable (ColumnScope.() -> Unit)
) {
    when {
        isVisible -> {
            Dialog(
                onDismissRequest = onDismissRequest,
            ) {
                Box {
                    Card(
                        modifier = modifier.widthIn(
                            min = 280.dp,
                            max = 560.dp
                        ),
                        shape = AlertDialogDefaults.shape,
                        colors = CardDefaults.cardColors(
                            AlertDialogDefaults.containerColor,
                            AlertDialogDefaults.textContentColor,
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                vertical = 24.dp
                            )
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        horizontal = 24.dp
                                    ).align(Alignment.CenterHorizontally),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    CompositionLocalProvider(
                                        value = LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                                        content = icon
                                    )

                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )

                                    if (text != null) {
                                        Text(
                                            text = text,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier.padding(contentPadding)
                                        .weight(1f, fill = false)
                                ) {
                                    content()
                                }
                                FlowRow(
                                    modifier = Modifier
                                        .padding(
                                            start = 24.dp,
                                            end = 24.dp,
                                            top = 8.dp
                                        )
                                        .align(Alignment.End),
                                    horizontalArrangement = Arrangement.spacedBy(
                                        8.dp,
                                        Alignment.End
                                    ),
                                ) {
                                    dismissButtonText?.let {
                                        TextButton(
                                            enabled = dismissButtonEnabled,
                                            onClick = onDismissRequest
                                        ) {
                                            Text(dismissButtonText)
                                        }
                                    }
                                    TextButton(
                                        enabled = confirmButtonEnabled,
                                        onClick = onConfirmRequest
                                    ) {
                                        Text(confirmButtonText)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}