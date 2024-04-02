package com.andtree.vdsina.ui.screens.monitoring

import java.util.Date

sealed class MonitoringEvent {
    data class onServerSelected(val serverId: Int): MonitoringEvent()
    data class onFromDateChange(val fromDate: String): MonitoringEvent()
    data class onToDateChange(val toDate: String): MonitoringEvent()
    data class onServerClick(val type: String): MonitoringEvent()
    data class onDateClick(val type: String): MonitoringEvent()
    data class onCurrentServerSelected(val id: Int, val name: String): MonitoringEvent()
    data class onDateRangeSelected(val range: String): MonitoringEvent()
    data class onServerOrderClick(val route: String): MonitoringEvent()
}