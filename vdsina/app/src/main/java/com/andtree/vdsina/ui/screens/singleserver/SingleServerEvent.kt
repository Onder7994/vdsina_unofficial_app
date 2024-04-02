package com.andtree.vdsina.ui.screens.singleserver

sealed class SingleServerEvent {
    data class onExit(val route: String): SingleServerEvent()
}