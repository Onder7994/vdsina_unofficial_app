package com.andtree.vdsina.ui.screens.settings

import android.annotation.SuppressLint
import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andtree.vdsina.R
import com.andtree.vdsina.utils.UiEvent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect{event ->
            when(event) {
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        event.message,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                } else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState){data ->
                Snackbar(
                    snackbarData = data,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .padding(bottom = 50.dp)
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(top = 70.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp, top = 15.dp)
            ) {
                SettingsContent(
                    text = "Уведомлять о низком балансе",
                    checkedItem = viewModel.isPushEnabled.value,
                    onCheckedChange = { isEnabled -> viewModel.onEvent(SettingsScreenEvent.onPushEnabled(isEnabled)) }
                )
                if (viewModel.isPushEnabled.value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Пороговое значение",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            )
                        )
                        TextField(
                            value = viewModel.balanceTreshold.value.toString(),
                            onValueChange = { input ->
                                val balance = input.toDoubleOrNull()
                                if (balance != null){
                                    viewModel.onEvent(SettingsScreenEvent.onBalanceTresholdChange(balance))
                                } else if (input.isEmpty()) {
                                    viewModel.onEvent(SettingsScreenEvent.onBalanceTresholdChange(1000.0))
                                }
                            },
                            maxLines = 1,
                            singleLine = true,
                            modifier = Modifier
                                .padding(start = 10.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.treshhold_icon),
                                    contentDescription = "treshold icon",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 20.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                SettingsContent(
                    text = "Включить темный режим",
                    checkedItem = viewModel.isDarkModeEnabled.value,
                    onCheckedChange = { isEnabled -> viewModel.onEvent(SettingsScreenEvent.onDarkModeEnabled(isEnabled)) }
                )
            }
        }
    }
}

@Composable
fun SettingsContent(
    text: String,
    checkedItem: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        )
        Switch(
            checked = checkedItem,
            onCheckedChange = { isEnabled -> onCheckedChange(isEnabled) },
            thumbContent = if (checkedItem) {
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