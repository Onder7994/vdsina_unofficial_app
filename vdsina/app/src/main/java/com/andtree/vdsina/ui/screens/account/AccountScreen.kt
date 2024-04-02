package com.andtree.vdsina.ui.screens.account

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andtree.vdsina.utils.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AccountScreen(
    viewModel: AccountScreenViewModel = hiltViewModel()
) {
    val accountLimits = viewModel.accountLimitsData.collectAsState().value
    val accountOperations = viewModel.accountOperationsData.collectAsState(initial = null).value ?: emptyList()
    val accountSingleOperation = viewModel.accountSingleOperationData.collectAsState().value
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

    LaunchedEffect(viewModel.startDateForPicker.value, viewModel.endDateForPicker.value){
        viewModel.startDateForPicker.value?.let { startDate ->
            viewModel.onEvent(AccountScreenEvent.onFromDateChange(startDate))
        }

        viewModel.endDateForPicker.value?.let { endDate ->
            viewModel.onEvent(AccountScreenEvent.onToDateChange(endDate))
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
            ) {
                AccountScreenUI(
                    viewModel = viewModel,
                    accountLimits = accountLimits,
                    accountOperations = accountOperations,
                    accountSingleOperation = accountSingleOperation
                )
            }
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
        }
    }
}