package com.andtree.vdsina.ui.screens.monitoring

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import com.andtree.vdsina.data.model.ServersData
import com.andtree.vdsina.data.model.serverstat.ServerListStatData
import com.andtree.vdsina.data.repository.VdsinaRepository
import com.andtree.vdsina.datastoremanager.DataStoreManager
import com.andtree.vdsina.utils.ApiResult
import com.andtree.vdsina.utils.UiEvent
import com.andtree.vdsina.utils.getCurrentDay
import com.andtree.vdsina.utils.getCurrentDayNumber
import com.andtree.vdsina.utils.getPastDay
import com.andtree.vdsina.utils.getPreviousMonthDateRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val repository: VdsinaRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {

    var isLoading by mutableStateOf(false)
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _filterOnDateMonitoringData = MutableStateFlow<List<ServerListStatData>?>(null)
    val filterOnDateMonitoringData = _filterOnDateMonitoringData.asStateFlow()

    private val _serversData = MutableStateFlow<List<ServersData>?>(null)

    var showBottomSheet = mutableStateOf(false)
    var bottomSheetType = mutableStateOf("")
    var monitoringFilterList = mutableListOf<Pair<Int, String>>()
    var selectedServerName = mutableStateOf("")

    var selectedServerId = mutableIntStateOf(0)
    var selectedDateRange = mutableStateOf("Сегодня")
    var fromDate = mutableStateOf(getCurrentDay())
    var toDate = mutableStateOf(getCurrentDay())
    var cpuPointsData = mutableStateOf<List<Point>?>(null)
    var diskWritesPointsData = mutableStateOf<List<Point>?>(null)
    var diskReadsPointsData = mutableStateOf<List<Point>?>(null)
    var vnetRxPointsData = mutableStateOf<List<Point>?>(null)
    var vnetTxPointsData = mutableStateOf<List<Point>?>(null)
    var vnetRxSumPointsData = mutableStateOf<List<Point>?>(null)
    var vnetTxSumPointsData = mutableStateOf<List<Point>?>(null)
    val dateRange = listOf<String>("Сегодня", "Вчера", "Последние 7 дней", "Текущий месяц", "Прошлый месяц")

    init {
        getAllServers()
        viewModelScope.launch {
            _serversData.filterNotNull().collect() { result ->
                result.forEach { item ->
                    monitoringFilterList.add(item.id to item.name)
                }
                if (monitoringFilterList.isNotEmpty()) {
                    selectedServerName.value = monitoringFilterList.first().second
                    selectedServerId.value  = monitoringFilterList.first().first
                    getMonitoringByDate()
                }
            }
        }
    }

    fun onEvent(event: MonitoringEvent){
        when(event) {
            is MonitoringEvent.onServerSelected -> {
                selectedServerId.value = event.serverId
            }
            is MonitoringEvent.onFromDateChange -> {
                fromDate.value = event.fromDate
            }
            is MonitoringEvent.onToDateChange -> {
                toDate.value = event.toDate
                if (selectedServerId.value != 0) {
                    getMonitoringByDate()
                }
            }
            is MonitoringEvent.onServerClick -> {
                bottomSheetType.value = event.type
                showBottomSheet.value = true
            }
            is MonitoringEvent.onDateClick -> {
                bottomSheetType.value = event.type
                showBottomSheet.value = true
            }
            is MonitoringEvent.onCurrentServerSelected -> {
                selectedServerName.value = event.name
                selectedServerId.value = event.id
                getMonitoringByDate()
            }
            is MonitoringEvent.onDateRangeSelected -> {
                selectedDateRange.value = event.range
                if (selectedDateRange.value == "Сегодня") {
                    fromDate.value = getCurrentDay()
                    toDate.value = getCurrentDay()
                    setEmptyPoints()
                    getMonitoringByDate()
                } else if (selectedDateRange.value == "Вчера") {
                    fromDate.value = getPastDay(1)
                    toDate.value = getCurrentDay()
                    setEmptyPoints()
                    getMonitoringByDate()
                } else if (selectedDateRange.value == "Последние 7 дней") {
                    fromDate.value = getPastDay(7)
                    toDate.value = getCurrentDay()
                    setEmptyPoints()
                    getMonitoringByDate()
                } else if (selectedDateRange.value == "Текущий месяц") {
                    fromDate.value = getPastDay(getCurrentDayNumber())
                    toDate.value = getCurrentDay()
                    setEmptyPoints()
                    getMonitoringByDate()
                } else if (selectedDateRange.value == "Прошлый месяц") {
                    val (startDate, endDate) = getPreviousMonthDateRange()
                    fromDate.value = startDate
                    toDate.value = endDate
                    setEmptyPoints()
                    getMonitoringByDate()
                }
            }
            is MonitoringEvent.onServerOrderClick -> {
                sendUiEvent(UiEvent.Navigate(event.route))
            }
        }
    }

    private fun getMonitoringByDate(){
        fetchData(
            dataFlow = _filterOnDateMonitoringData,
            dataRequest = { repository.getServerStatsByDate(dataStoreManager.tokenFLow.first()!!, selectedServerId.value, fromDate.value, toDate.value) },
            extractData = { response -> response.data }
        )
    }

    private fun getAllServers() {
        fetchData(
            dataFlow = _serversData,
            dataRequest = { repository.getAllServers(dataStoreManager.tokenFLow.first()!!) },
            extractData = { response -> response.data }
        )
    }

    private fun <T : Any, R> fetchData(
        dataFlow: MutableStateFlow<R?>,
        dataRequest: suspend () -> Flow<ApiResult<T>>,
        extractData: (T) -> R
    ) {
        viewModelScope.launch {
            isLoading = true
            dataRequest().collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        dataFlow.value = extractData(result.data)
                        isLoading = false
                    }
                    is ApiResult.Error -> {
                        sendUiEvent(UiEvent.ShowSnackBar(result.errorMessage))
                        isLoading = false
                    }
                    is ApiResult.Loading -> {
                        isLoading = true
                    }
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }

    private fun setEmptyPoints() {
        cpuPointsData.value = null
        diskReadsPointsData.value = null
        diskWritesPointsData.value = null
        vnetRxPointsData.value = null
        vnetTxPointsData.value = null
    }
}