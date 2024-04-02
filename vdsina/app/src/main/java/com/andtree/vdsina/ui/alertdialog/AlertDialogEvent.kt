package com.andtree.vdsina.ui.alertdialog

sealed class AlertDialogEvent {
    object onCancel: AlertDialogEvent()
    object onConfirm: AlertDialogEvent()
    data class onTextChange(val text: String): AlertDialogEvent()
    data class onIsChecked(val isChecked: Boolean): AlertDialogEvent()
}