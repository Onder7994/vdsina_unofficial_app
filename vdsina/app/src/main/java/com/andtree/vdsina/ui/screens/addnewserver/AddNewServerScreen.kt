package com.andtree.vdsina.ui.screens.addnewserver

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.andtree.vdsina.ui.alertdialog.MainAlertDialog
import com.andtree.vdsina.ui.components.MainTopAppBar
import com.andtree.vdsina.ui.theme.White
import com.andtree.vdsina.utils.ScreenRoutes
import com.andtree.vdsina.utils.UiEvent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewServerScreen(
    mainNavController: NavHostController,
    viewModel: AddNewServerViewModel = hiltViewModel()
) {
    val localNavController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val datacenterData = viewModel.datacenterData.collectAsState(initial = null).value ?: emptyList()
    val serverPlanData = viewModel.serverPlanData.collectAsState(initial = null).value ?: emptyList()
    val templateData = viewModel.templateData.collectAsState(initial = null).value ?: emptyList()
    val sshKeyData = viewModel.sshKeyData.collectAsState(initial = null).value ?: emptyList()
    val serverGroupData = viewModel.serverGroupData.collectAsState(initial = null).value ?: emptyList()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val isoImageData = viewModel.isoImageData.collectAsState(initial = null).value ?: emptyList()
    val backupData = viewModel.backupData.collectAsState(initial = null).value ?: emptyList()
    
    LaunchedEffect(key1 = true){
        viewModel.uiEvent.collect{event ->
            when(event){
                is UiEvent.Navigate -> {
                    localNavController.navigate(event.route)
                }
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        event.message,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
                is UiEvent.NavigateMain -> {
                    mainNavController.navigate(event.route)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier
                        .padding(bottom = 50.dp)
                )
            }
        },
        topBar = {
            MainTopAppBar(
                onExitClick = { viewModel.onEvent(AddNewServerEvent.onExit(ScreenRoutes.LOGIN_SCREEN)) },
                title = "Vdsina - Заказ сервера",
                scrollBehavior = scrollBehavior
            )
        })
    {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(top = 70.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item {
                        AddNewServersUI(
                            datacenterData = datacenterData,
                            serverPlanData = serverPlanData,
                            templateData = templateData,
                            sshKeyData = sshKeyData,
                            serverGroupData = serverGroupData,
                            isoImageData = isoImageData,
                            backupData = backupData,
                            viewModel = viewModel
                        )
                    }
                }
            }
            MainAlertDialog(alertDialogController = viewModel)
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