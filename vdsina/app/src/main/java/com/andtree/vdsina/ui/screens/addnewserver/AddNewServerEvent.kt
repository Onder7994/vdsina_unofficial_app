package com.andtree.vdsina.ui.screens.addnewserver

import com.andtree.vdsina.data.model.CreateServerResponseData

sealed class AddNewServerEvent {
    object onCreationConfirm: AddNewServerEvent()
    data class onHostNameChange(val host: String): AddNewServerEvent()
    data class onNameChange(val name: String): AddNewServerEvent()
    data class onCpuChange(val cpu: Int): AddNewServerEvent()
    data class onRamChange(val ram: Int): AddNewServerEvent()
    data class onDiskChange(val disk: Int): AddNewServerEvent()
    data class onIpChange(val isIpChecked: Boolean): AddNewServerEvent()
    data class onExit(val route: String): AddNewServerEvent()
    data class onOsTemplateSelected(val itemId: Int, val minCpu: Float, val minRam: Float): AddNewServerEvent()
    data class onServerGroupSelected(val itemId: Int): AddNewServerEvent()
    data class onServerPlanSelected(val itemId: Int): AddNewServerEvent()
    data class onDatacenterSelected(val itemId: Int): AddNewServerEvent()
    data class onSshKeyIdSelected(val itemId: Int): AddNewServerEvent()
    data class onIsoImageSelected(val itemId: Int): AddNewServerEvent()
    data class onBackupSelected(val itemId: Int): AddNewServerEvent()
    object onClearButtomClick: AddNewServerEvent()
    object onSshCleanClick: AddNewServerEvent()
}