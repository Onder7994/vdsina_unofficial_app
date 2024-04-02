package com.andtree.vdsina.ui.screens.singleserver

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.navigation.NavController
import com.andtree.vdsina.ui.components.MainTopAppBar
import com.andtree.vdsina.ui.screens.addnewserver.AddNewServerEvent
import com.andtree.vdsina.ui.theme.CardBackgroundColor
import com.andtree.vdsina.ui.theme.Green
import com.andtree.vdsina.ui.theme.White
import com.andtree.vdsina.utils.ScreenRoutes
import com.andtree.vdsina.utils.UiEvent
import kotlinx.coroutines.flow.collect

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SingleServerScreen(
    navController: NavController,
    viewModel: SingleServerViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val singleServerData = viewModel.singleServerData.collectAsState().value
    val isRefreshing = viewModel.isRefreshing.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = { viewModel.refreshScreen() }
    )

    LaunchedEffect(key1 = true){
        viewModel.uiEvent.collect{ event ->
            when(event){
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        event.message,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
                is UiEvent.NavigateMain -> {
                    navController.navigate(event.route)
                } else -> {}
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
                onExitClick = { viewModel.onEvent(SingleServerEvent.onExit(ScreenRoutes.LOGIN_SCREEN)) },
                title = "Vdsina - Сервер ${singleServerData?.name}",
                scrollBehavior = scrollBehavior
            )
        })
    {
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
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item {
                        SingleServerCard(singleServerData)
                    }
                }
            }
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