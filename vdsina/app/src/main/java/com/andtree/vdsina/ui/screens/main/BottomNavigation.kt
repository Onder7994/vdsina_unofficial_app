package com.andtree.vdsina.ui.screens.main

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.andtree.vdsina.ui.theme.UnselectedGray

@Composable
fun BottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val listMenuItems = listOf(
        BottonNavigationItems.MainServers,
        BottonNavigationItems.MonitoringServers,
        BottonNavigationItems.AccountDetails,
        BottonNavigationItems.Settings
    )
    BottomNavigation(backgroundColor = Color.White) {
        listMenuItems.forEach{ buttonNameItem ->
            BottomNavigationItem(
                selected = currentRoute == buttonNameItem.route,
                onClick = { onNavigate(buttonNameItem.route) },
                icon = {
                    Icon(painter = painterResource(id = buttonNameItem.iconId), contentDescription = "icon")
                },
                label = {
                    Text(text = buttonNameItem.title)
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = UnselectedGray
            )
        }
    }
}