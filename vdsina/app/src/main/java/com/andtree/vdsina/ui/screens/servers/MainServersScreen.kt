package com.andtree.vdsina.ui.screens.servers

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.andtree.vdsina.R
import com.andtree.vdsina.ui.alertdialog.MainAlertDialog
import com.andtree.vdsina.ui.theme.CardBackgroundColor
import com.andtree.vdsina.ui.theme.Green
import com.andtree.vdsina.ui.theme.White
import com.andtree.vdsina.utils.UiEvent
import com.andtree.vdsina.utils.dateFormatted

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun MainServersScreen(
    viewModel: MainServersViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val balanceData = viewModel.balanceData.collectAsState().value
    val accountData = viewModel.accountData.collectAsState().value
    val accountConvertDate = viewModel.dateFormatted(accountData?.forecast ?: "Загрузка")
    val serversList = viewModel.serversData.collectAsState(initial = null).value ?: emptyList()
    val isRefreshing = viewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = { viewModel.refreshScreen() }
    )
    
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect{event ->
            when(event) {
                is UiEvent.Navigate -> {
                    onNavigate(event.route)
                }
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
    ){
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(top = 70.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
            ) {
                BalanceCardScreen(balanceData, accountConvertDate)
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(serversList) { serversData ->
                        ServersCardScreen(serversData) {event ->
                            viewModel.onEvent(event)
                        }
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
            PullRefreshIndicator(
                refreshing = isRefreshing.value,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}