package com.andtree.vdsina.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.andtree.vdsina.R
import com.andtree.vdsina.ui.components.MainTopAppBar
import com.andtree.vdsina.ui.navigation.NavigationGraph
import com.andtree.vdsina.ui.theme.White
import com.andtree.vdsina.utils.ScreenRoutes
import com.andtree.vdsina.utils.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    mainNavController: NavHostController,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val localNavController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry = localNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var title = mutableStateOf("")

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect{uiEvent ->
            when(uiEvent) {
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        uiEvent.message,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
                is UiEvent.Navigate -> {
                    localNavController.navigate(uiEvent.route)
                }
                is UiEvent.NavigateMain -> {
                    mainNavController.navigate(uiEvent.route)
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
        floatingActionButton = {
            Box() {
                if (viewModel.showFloatingActionBottom.value) FloatingActionButton(
                    onClick = { viewModel.onEvent(MainScreenEvent.OnNewServerClick(currentRoute ?: ScreenRoutes.MAIN_SERVERS)) },
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 5.dp
                    ),
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(60.dp)
                        .offset(y = 50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_server_icon),
                        contentDescription = "Add icon",
                        tint = Color.White
                    )
                }
            }
        },
        topBar = {
            when(currentRoute) {
                ScreenRoutes.MAIN_SERVERS -> {
                    title.value = "Vdsina - Сервера"
                }
                ScreenRoutes.MONITORING_SERVERS -> {
                    title.value = "Vdsina - Мониторинг"
                }
                ScreenRoutes.ACCOUNT_INFORMATIONS -> {
                    title.value = "Vdsina - Профиль"
                }
                ScreenRoutes.SETTINGS_SCREEN -> {
                    title.value = "Vdsina - Настройки"
                }
            }
            MainTopAppBar(
                onExitClick = { viewModel.onEvent(MainScreenEvent.OnExit(ScreenRoutes.LOGIN_SCREEN)) },
                title = title.value,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomNavigation(currentRoute = currentRoute) {route ->
                viewModel.onEvent(MainScreenEvent.Navigate(route))
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        NavigationGraph(navController =  localNavController) {route ->
            viewModel.onEvent(MainScreenEvent.MainNavigate(route))
        }
    }
}