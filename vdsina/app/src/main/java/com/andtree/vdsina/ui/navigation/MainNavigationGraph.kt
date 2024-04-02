package com.andtree.vdsina.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.andtree.vdsina.ui.screens.account.AccountScreen
import com.andtree.vdsina.ui.screens.addnewserver.AddNewServerScreen
import com.andtree.vdsina.ui.screens.login.LoginScreen
import com.andtree.vdsina.ui.screens.main.MainScreen
import com.andtree.vdsina.ui.screens.monitoring.MonitoringScreen
import com.andtree.vdsina.ui.screens.settings.SettingsScreen
import com.andtree.vdsina.ui.screens.singleserver.SingleServerScreen
import com.andtree.vdsina.ui.theme.White
import com.andtree.vdsina.utils.ScreenRoutes

@Composable
fun MainNavigationGraph(
    startDestinationViewModel: StartDestinationViewModel = hiltViewModel()
) {
    val navHostController = rememberNavController()
    val startDestination = startDestinationViewModel.startDestination.value
    val isLoading = startDestinationViewModel.isLoading.value
    if (isLoading) {
        Box(
            modifier = Modifier
                .background(White)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        NavHost(navController = navHostController, startDestination = startDestination){
            composable(ScreenRoutes.LOGIN_SCREEN){
                LoginScreen(navHostController)
            }
            composable(ScreenRoutes.MAIN_SCREEN){
                MainScreen(navHostController)
            }
            composable(ScreenRoutes.ADD_NEW_SERVER_SCREEN) {
                AddNewServerScreen(navHostController)
            }
            composable(ScreenRoutes.SINGLE_SERVER_SCREEN + "/{serverId}") {
                SingleServerScreen(navHostController)
            }
        }
    }
}