package com.lumopos.display.compose.dialog.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * An abstract base class for managing the state of a dialog in a Compose UI.
 *
 * This class provides the fundamental properties and methods to control a dialog's visibility
 * and handle common dialog actions like dismissal and confirmation. Subclasses should be
 * created to manage the state for specific types of dialogs, potentially adding more
 * state properties relevant to that dialog's content.
 *
 * @property onDismissRequest A lambda function to be executed when a dismissal event is triggered,
 *   such as clicking outside the dialog or pressing the back button. Defaults to an empty lambda.
 * @property onConfirmRequest A lambda function to be executed when a confirmation event is triggered,
 *   such as clicking a "Confirm" or "OK" button. Defaults to an empty lambda.
 */
open class LumoDialogState(
    private val onDismissRequest: () -> Unit = {},
    private val onConfirmRequest: () -> Unit = {}
) {
    /**
     * Controls the visibility of the dialog.
     * `true` to show the dialog, `false` to hide it.
     * Its state is managed internally by [openDialog] and [closeDialog].
     */
    var isVisible: Boolean by mutableStateOf(false)
        private set
    var isConfirmEnabled: Boolean by mutableStateOf(true)
        protected set
    var isDismissEnabled: Boolean by mutableStateOf(true)
        protected set

    /**
     * Opens the dialog by setting its visibility to true.
     */
    open fun openDialog() {
        isVisible = true
    }

    /**
     * Closes the dialog by setting its visibility state to false.
     * This will hide the dialog from the UI.
     */
    open fun closeDialog() {
        isVisible = false
    }

    open fun confirm() {
        onConfirmRequest()
        closeDialog()
    }

    open fun dismiss() {
        onDismissRequest()
        closeDialog()
    }
}