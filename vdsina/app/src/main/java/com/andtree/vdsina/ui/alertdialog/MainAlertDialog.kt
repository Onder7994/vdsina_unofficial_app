package com.andtree.vdsina.ui.alertdialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andtree.vdsina.ui.theme.AlertDialogFocusedColor
import com.andtree.vdsina.ui.theme.MainBlack

@Composable
fun MainAlertDialog(alertDialogController: AlertDialogController) {
    if (alertDialogController.openAlertDialog.value) {
        AlertDialog(
            onDismissRequest = { alertDialogController.onAlertDialogEvent(AlertDialogEvent.onCancel) },
            title = null,
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = alertDialogController.alertDialogTitle.value,
                        style = TextStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (alertDialogController.showEditText.value) {
                        TextField(
                            value = alertDialogController.alertDialogText.value,
                            onValueChange = {
                                alertDialogController.onAlertDialogEvent(AlertDialogEvent.onTextChange(it))
                            },
                            placeholder = {
                                Text(text = "Новое имя сервера")
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = AlertDialogFocusedColor
                            ),
                            shape = RoundedCornerShape(5.dp),
                            textStyle = TextStyle(
                                color = MainBlack,
                                fontSize = 16.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (alertDialogController.showChecked.value) {
                        Column(

                        ) {
                            Text(text = "Подключить автопродление?")
                            Spacer(modifier = Modifier.width(5.dp))
                            Switch(
                                checked = alertDialogController.isChecked.value,
                                onCheckedChange = { alertDialogController.onAlertDialogEvent(AlertDialogEvent.onIsChecked(it)) },
                                thumbContent = if (alertDialogController.isChecked.value) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Check icon",
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    null
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { alertDialogController.onAlertDialogEvent(AlertDialogEvent.onConfirm) }) {
                    Text(text = "Oк")
                }
            },
            dismissButton = {
                TextButton(onClick = { alertDialogController.onAlertDialogEvent(AlertDialogEvent.onCancel) }) {
                    Text(text = "Отмена")
                }
            }
        )
    }
}