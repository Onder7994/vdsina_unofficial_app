package com.andtree.vdsina.ui.screens.settings

sealed class SettingsScreenEvent {
    data class onPushEnabled(val isEnabled: Boolean): SettingsScreenEvent()
    data class onDarkModeEnabled(val isEnabled: Boolean): SettingsScreenEvent()
    data class onBalanceTresholdChange(val balance: Double): SettingsScreenEvent()
}