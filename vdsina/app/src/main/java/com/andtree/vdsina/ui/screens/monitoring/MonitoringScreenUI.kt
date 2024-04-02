package com.andtree.vdsina.ui.screens.monitoring

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.extensions.formatToSinglePrecision
import co.yml.charts.common.extensions.isNotNull
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.andtree.vdsina.R
import com.andtree.vdsina.data.model.serverstat.ServerListStatData
import com.andtree.vdsina.ui.components.MainModalButtonSheet
import com.andtree.vdsina.utils.ScreenRoutes
import com.andtree.vdsina.utils.getPointsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringScreenUI(
    monitoringData: List<ServerListStatData>,
    viewModel: MonitoringViewModel
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    HeaderMonitoring(viewModel)
    if (viewModel.showBottomSheet.value) {
        LaunchedEffect(key1 = viewModel.showBottomSheet.value) {
            scope.launch {
                sheetState.show()
            }
        }
        MainModalButtonSheet(
            scope = scope,
            sheetState = sheetState,
            showButtonSheet = viewModel.showBottomSheet,
            buttonText = "Сохранить"
        ) {
            if (viewModel.bottomSheetType.value == "date"){
                if (viewModel.monitoringFilterList.isNotEmpty()) {
                    ContentRow(
                        iconRes = R.drawable.date_range_icon,
                        items = viewModel.dateRange.map { it to it },
                        selectedItem = viewModel.selectedDateRange.value,
                        onSelectItem = {_, text -> viewModel.onEvent(MonitoringEvent.onDateRangeSelected(text))}
                    )
                } else {
                    Text(
                        text = "Сервера не найдены",
                        modifier = Modifier.padding(start = 20.dp, bottom = 20.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            } else {
                if (viewModel.monitoringFilterList.isNotEmpty()) {
                    ContentRow(
                        iconRes = R.drawable.laptop_icon,
                        items = viewModel.monitoringFilterList.map { it.first to it.second },
                        selectedItem = viewModel.selectedServerName.value,
                        onSelectItem = { id, name -> viewModel.onEvent(MonitoringEvent.onCurrentServerSelected(id, name)) }
                    )
                } else {
                    Text(
                        text = "Сервера не найдены",
                        modifier = Modifier.padding(start = 20.dp, bottom = 20.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }
        }
    } else {
        LaunchedEffect(key1 = viewModel.showBottomSheet.value) {
            scope.launch {
                sheetState.hide()
            }
        }
    }
    if (viewModel.monitoringFilterList.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                HeadTitle(text = "Использование CPU")
                ChartsDraw(
                    monitoringData = monitoringData,
                    statePointsData = viewModel.cpuPointsData,
                    listType = "cpu",
                    yValueType = "%",
                    popUpLabel = { x, y ->
                        val xLabel = "Процессор"
                        val yLabel = "${String.format("%.2f", y)}"
                        "$xLabel $yLabel %"
                    }
                )
                HeadTitle(text = "Операции с диском")
                ChartsDraw(
                    monitoringData = monitoringData,
                    statePointsData = viewModel.diskWritesPointsData,
                    listType = "disk_w",
                    yValueType = "iops",
                    secondPointsData = viewModel.diskReadsPointsData,
                    secondListType = "disk_r",
                    popUpLabel = {x, y ->
                        val xLabel = "Чтение с диска"
                        val yLabel = "${String.format("%.2f", y)}"
                        "$xLabel $yLabel iops"
                    },
                    popUpLabelSecond = {x, y ->
                        val xLabel = "Запись на диск"
                        val yLabel = "${String.format("%.2f", y)}"
                        "$xLabel $yLabel iops"
                    }
                )
                HeadTitle(text = "Передача данных по сети")
                ChartsDraw(
                    monitoringData = monitoringData,
                    statePointsData = viewModel.vnetRxPointsData,
                    listType = "vnet_rx",
                    yValueType = "Mbps",
                    popUpLabel = {x, y ->
                        val xLabel = "Исходящий трафик"
                        val yLabel = "${String.format("%.4f", y)}"
                        "$xLabel $yLabel Mbps"
                    },
                    secondPointsData = viewModel.vnetTxPointsData,
                    secondListType = "vnet_tx",
                    popUpLabelSecond = {x, y ->
                        val xLabel = "Входящий трафик"
                        val yLabel = "${String.format("%.4f", y)}"
                        "$xLabel $yLabel Mbps"
                    }
                )
            }
        }
    } else {
        HeadTitle(text = "Сервера не найдены")
        Button(
            onClick = { viewModel.onEvent(MonitoringEvent.onServerOrderClick(ScreenRoutes.ADD_NEW_SERVER_SCREEN)) },
            modifier = Modifier
                .padding(top = 15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 15.dp)
        ) {
            Text(
                text = "Заказать сервер",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun HeaderMonitoring(
    viewModel: MonitoringViewModel
) {
    HeadTitle(text = "Параметры мониторинга")
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        item {
            Button(
                onClick = { viewModel.onEvent(MonitoringEvent.onDateClick("date")) },
                modifier = Modifier
                    .padding(top = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 15.dp)
            ){
                Icon(
                    painter = painterResource(id = R.drawable.date_range_icon),
                    contentDescription = "Date range"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = viewModel.selectedDateRange.value,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Button(
                onClick = { viewModel.onEvent(MonitoringEvent.onServerClick("server")) },
                modifier = Modifier
                    .padding(top = 15.dp, start = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 15.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.servers_icon),
                    contentDescription = "Server icon"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = viewModel.selectedServerName.value,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
@Composable
fun HeadTitle(
    text: String
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 20.dp, top = 15.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun <T> ContentRow(
    iconRes: Int,
    items: List<Pair<T, String>>,
    selectedItem: String,
    onSelectItem: (T, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        items.forEach { (id, name) ->
            Row(
                modifier = Modifier
                    .clickable { onSelectItem(id, name) }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 15.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                RadioButton(
                    selected = selectedItem == name,
                    onClick = { onSelectItem(id, name) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            Divider(modifier = Modifier.fillMaxWidth())
        }
    }
}


@Composable
fun ChartsDraw(
    monitoringData: List<ServerListStatData>?,
    statePointsData: MutableState<List<Point>?>,
    listType: String,
    yValueType: String,
    popUpLabel: (Float, Float) -> String,
    popUpLabelSecond: ((Float, Float) -> String)? = null,
    secondPointsData: MutableState<List<Point>?>? = null,
    secondListType: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(top = 10.dp)
    ) {
        LaunchedEffect(monitoringData) {
            if (!monitoringData.isNullOrEmpty()) {
                statePointsData.value = getPointsList(monitoringData, listType)
                if (secondPointsData != null && secondListType != null) {
                    secondPointsData.value = getPointsList(monitoringData, secondListType)
                }
            }
        }
        statePointsData.value?.let { pointsData ->
            val timeLabels = monitoringData?.map { it.dt }
            ChartConfigure(
                pointsData = pointsData,
                timeLabels = timeLabels,
                yValueType = yValueType,
                popUpLabel = popUpLabel,
                popUpLabelSecond = popUpLabelSecond,
                secondLinePointsData = secondPointsData?.value
            )
        }
    }
}

@Composable
fun ChartConfigure(
    pointsData: List<Point>,
    timeLabels: List<String>?,
    yValueType: String,
    popUpLabel: (Float, Float) -> String,
    popUpLabelSecond: ((Float, Float) -> String)? = null,
    secondLinePointsData: List<Point>? = null
) {
    val sortedPointDate = pointsData.sortedBy { it.y }
    var steps: Int = 1
    if (pointsData.size - 1 > 24) {
        steps = 24
    } else {
        steps = pointsData.size - 1
    }
    val xAxisData = AxisData.Builder()
        .axisStepSize(200.dp)
        .backgroundColor(MaterialTheme.colorScheme.surface)
        .steps(pointsData.size - 1)
        .labelData { i ->
            timeLabels?.get(i) ?: i.toString()
        }
        .labelAndAxisLinePadding(15.dp)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(MaterialTheme.colorScheme.surface)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            if (yValueType == "Mbps") {
                (String.format("%.3f", sortedPointDate[i].y)) + " " + yValueType
            } else {
                (sortedPointDate[i].y).formatToSinglePrecision()+ " " + yValueType
            }
        }.build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOfNotNull(
                Line(
                    dataPoints = pointsData,
                    LineStyle(
                        color = MaterialTheme.colorScheme.primary,
                        width = 8f
                    ),
                    IntersectionPoint(
                        color = MaterialTheme.colorScheme.primary,
                        radius = 5.dp
                    ),
                    SelectionHighlightPoint(
                        color = MaterialTheme.colorScheme.primary,
                        radius = 5.dp
                    ),
                    ShadowUnderLine(
                        color = MaterialTheme.colorScheme.surfaceTint
                    ),
                    SelectionHighlightPopUp(
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        labelColor = MaterialTheme.colorScheme.onPrimary,
                        popUpLabel = popUpLabel
                    )
                ),
                secondLinePointsData?.let {
                    Line(
                        dataPoints = it,
                        LineStyle(
                            color = MaterialTheme.colorScheme.inversePrimary,
                            width = 8f
                        ),
                        IntersectionPoint(
                            color = MaterialTheme.colorScheme.inversePrimary,
                            radius = 5.dp
                        ),
                        SelectionHighlightPoint(
                            color = MaterialTheme.colorScheme.inversePrimary,
                            radius = 5.dp
                        ),
                        ShadowUnderLine(
                            color = MaterialTheme.colorScheme.surfaceTint
                        ),
                        if (popUpLabelSecond != null) {
                            SelectionHighlightPopUp(
                                backgroundColor = MaterialTheme.colorScheme.inversePrimary,
                                labelColor = MaterialTheme.colorScheme.onPrimary,
                                popUpLabel = popUpLabelSecond
                            )
                        } else {
                            SelectionHighlightPopUp()
                        }
                    )
                }
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(
            enableHorizontalLines = false,
            enableVerticalLines = false
        ),
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        lineChartData = lineChartData
    )
}