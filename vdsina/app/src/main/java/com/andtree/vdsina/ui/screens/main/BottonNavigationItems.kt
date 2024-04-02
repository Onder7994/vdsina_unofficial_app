package com.andtree.vdsina.ui.screens.main

import com.andtree.vdsina.R
import com.andtree.vdsina.utils.ScreenRoutes

sealed class BottonNavigationItems(val title: String, val iconId: Int, val route: String) {
    object MainServers: BottonNavigationItems("Мои сервера", R.drawable.servers_icon, ScreenRoutes.MAIN_SERVERS)
    object MonitoringServers: BottonNavigationItems("Мониторинг", R.drawable.monitoring_icon, ScreenRoutes.MONITORING_SERVERS)
    object AccountDetails: BottonNavigationItems("Профиль", R.drawable.account_icon, ScreenRoutes.ACCOUNT_INFORMATIONS)
    object Settings: BottonNavigationItems("Настройки", R.drawable.settings_icon, ScreenRoutes.SETTINGS_SCREEN)
}