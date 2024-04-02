package com.andtree.vdsina.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.andtree.vdsina.ui.screens.account.AccountScreen
import com.andtree.vdsina.ui.screens.monitoring.MonitoringScreen
import com.andtree.vdsina.ui.screens.servers.MainServersScreen
import com.andtree.vdsina.ui.screens.settings.SettingsScreen
import com.andtree.vdsina.utils.ScreenRoutes

@Composable
fun NavigationGraph(navController: NavHostController, onNavigate: (String) -> Unit) {
    NavHost(navController = navController, startDestination = ScreenRoutes.MAIN_SERVERS) {
        composable(ScreenRoutes.MAIN_SERVERS){
            MainServersScreen() {route ->
                onNavigate(route)
            }
        }
        composable(ScreenRoutes.MONITORING_SERVERS) {
            MonitoringScreen() {route ->
                onNavigate(route)
            }
        }
        composable(ScreenRoutes.ACCOUNT_INFORMATIONS) {
            AccountScreen()
        }
        composable(ScreenRoutes.SETTINGS_SCREEN) {
            SettingsScreen()
        }
    }
}