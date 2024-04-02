package com.andtree.vdsina.ui.alertdialog

import androidx.compose.runtime.MutableState

interface AlertDialogController {
    val alertDialogTitle: MutableState<String>
    val openAlertDialog: MutableState<Boolean>
    val alertDialogText: MutableState<String>
    val showEditText: MutableState<Boolean>
    val isChecked: MutableState<Boolean>
    val showChecked: MutableState<Boolean>
    fun onAlertDialogEvent(event: AlertDialogEvent)
}