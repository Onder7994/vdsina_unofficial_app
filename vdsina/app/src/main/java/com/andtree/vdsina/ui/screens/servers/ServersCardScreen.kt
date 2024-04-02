package com.andtree.vdsina.ui.screens.servers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.extensions.formatToSinglePrecision
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.andtree.vdsina.R
import com.andtree.vdsina.data.model.ServersData
import com.andtree.vdsina.ui.theme.CardBackgroundColor
import com.andtree.vdsina.ui.theme.Green
import com.andtree.vdsina.ui.theme.Red
import com.andtree.vdsina.utils.ScreenRoutes
import com.andtree.vdsina.utils.VerticalProgressHelper

@Composable
fun ServersCardScreen(
    serversData: ServersData,
    onEvent: (MainServersEvent) -> Unit
) {
    val progress = VerticalProgressHelper.getProgress(serversData.status)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 15.dp)
            .clickable { onEvent(MainServersEvent.OnServerClick("${ScreenRoutes.SINGLE_SERVER_SCREEN}/${serversData.id}")) },
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(modifier = Modifier
            .padding(5.dp)
        ) {
            VerticalProgressIndicator(
                progress = progress,
                color = VerticalProgressHelper.getProgressColor(progress),
                modifier = Modifier
                    .padding(start = 2.dp, top = 10.dp)
                    .size(width = 1.dp, height = 120.dp)
            )
            Column(
                modifier = Modifier
                    .padding(top = 8.dp, start = 5.dp)
                    .weight(1f)
            ) {
                ServersCardData(text = "Имя сервера: ", serverInformation = serversData.name)
                ServersCardData(text = "IP адрес: ", serverInformation = serversData.ip?.ip ?: "не назначен")
                ServersCardData(text = "ОС: ", serverInformation = serversData.template.name)
                ServersCardData(text = "Локация: ", serverInformation = serversData.datacenter.country)
                ServersCardData(text = "Статус: ", serverInformation = serversData.status)
            }
            Column() {
                IconButton(
                    onClick = { onEvent(MainServersEvent.OnShowEditDialog(serversData.id)) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_icon),
                        contentDescription = "Edit icon",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
                IconButton(
                    onClick = { onEvent(MainServersEvent.OnShowRebootDialog(serversData.id)) },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.restart_icon),
                        contentDescription = "Restart icon",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
                IconButton(
                    onClick = { onEvent(MainServersEvent.OnShowDeleteDialog(serversData.id)) },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete_icon),
                        contentDescription = "Delete icon",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

        }
    }
}

@Composable
fun ServersCardData(
    text: String,
    serverInformation: String
) {
    Row {
        Text(
            text = text,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = serverInformation,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun VerticalProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f),
    strokeWidth: Float = 10f
) {
    Canvas(modifier = modifier) {
        val yStart = size.height
        val yEnd = size.height * (1 - progress)

        drawLine(
            color = trackColor,
            start = Offset(x = size.width / 2, y = 0f),
            end = Offset(x = size.width / 2, y = size.height),
            strokeWidth = strokeWidth
        )

        drawLine(
            color = color,
            start = Offset(x = size.width / 2, y = yStart),
            end = Offset(x = size.width / 2, y = yEnd),
            strokeWidth = strokeWidth
        )
    }
}