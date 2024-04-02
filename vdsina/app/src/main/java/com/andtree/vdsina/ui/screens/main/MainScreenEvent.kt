package com.andtree.vdsina.ui.screens.main

sealed class MainScreenEvent {
    data class Navigate(val route: String): MainScreenEvent()
    data class MainNavigate(val route: String): MainScreenEvent()
    data class OnExit(val route: String): MainScreenEvent()
    data class OnNewServerClick(val route: String): MainScreenEvent()
}