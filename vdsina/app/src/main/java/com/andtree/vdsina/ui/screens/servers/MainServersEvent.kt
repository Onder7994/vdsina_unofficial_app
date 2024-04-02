package com.andtree.vdsina.ui.screens.servers

sealed class MainServersEvent {
    data class OnServerClick(val route: String): MainServersEvent()
    data class OnShowDeleteDialog(val serverId: Int): MainServersEvent()
    data class OnShowRebootDialog(val serverId: Int): MainServersEvent()
    data class OnShowEditDialog(val serverId: Int): MainServersEvent()
}