package com.andtree.vdsina.utils

import android.util.Log
import androidx.lifecycle.ViewModel
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import com.andtree.vdsina.data.model.serverstat.ChartsPoints
import com.andtree.vdsina.data.model.serverstat.ServerListStatData
import java.nio.channels.Pipe
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun ViewModel.dateFormatted(sourceDate: String): String {
    if (sourceDate == "Загрузка") return sourceDate
    val sourceDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val targetDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val date = LocalDate.parse(sourceDate, sourceDateFormatter)

    return date.format(targetDateFormatter)
}

fun getCurrentDay(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    return formatter.format(calendar.time)
}

fun getPastDay(days: Int): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, - days)
    return formatter.format(calendar.time)
}

fun getCurrentDateInDateFormat(): Date {
    val calendar = Calendar.getInstance()
    return calendar.time
}

fun getLastDateInDateFormat(date: String): Date {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return format.parse(date)
}

fun convertDateToString(date: Date): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(date)
}

fun getPreviousMonthDateRange(): Pair<String, String> {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val startCalendar = Calendar.getInstance().apply {
        add(Calendar.MONTH, -1)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val startDate = formatter.format(startCalendar.time)

    val endCalendar = Calendar.getInstance().apply {
        add(Calendar.MONTH, -1)
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    }
    val endDate = formatter.format(endCalendar.time)

    return Pair(startDate, endDate)
}

fun getCurrentDayNumber(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.DAY_OF_MONTH)
}

fun convertDateTimeToHoursSinceStart(dateTimeString: String, startDateTimeString: String): Float {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val startDate = format.parse(startDateTimeString) ?: return 0f
    val currentDate = format.parse(dateTimeString) ?: return 0f
    val difference = currentDate.time - startDate.time
    return (difference / (1000 * 60 * 60)).toFloat()
}

fun converRangeDateToHumanReadable(startDateStr: String, endDateStr: String): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormatter = SimpleDateFormat("dd MMM EE", Locale("ru"))
    val startDate = formatter.parse(startDateStr)
    val endDate = formatter.parse(endDateStr)
    return "${outputFormatter.format(startDate)} - ${outputFormatter.format(endDate)}"
}

fun getPointsList(serverListStatData: List<ServerListStatData>, listType: String): List<Point> {
    val pointsList = mutableListOf<Point>()
    val startDate = serverListStatData.first().dt
    val timePeriod = 3600
    serverListStatData.forEach { item ->
        if (listType == "cpu") {
            pointsList.add(
                Point(
                    convertDateTimeToHoursSinceStart(item.dt, startDate),
                    item.stat.cpu
                )
            )
        } else if (listType == "disk_w") {
            pointsList.add(
                Point(
                    convertDateTimeToHoursSinceStart(item.dt, startDate),
                    item.stat.diskWrites.toFloat() / timePeriod
                )
            )
        } else if (listType == "disk_r") {
            pointsList.add(
                Point(
                    convertDateTimeToHoursSinceStart(item.dt, startDate),
                    item.stat.diskReads.toFloat() / timePeriod
                )
            )
        } else if (listType == "vnet_rx") {
            pointsList.add(
                Point(
                    convertDateTimeToHoursSinceStart(item.dt, startDate),
                    (item.stat.vnetRx * 8) / 1024.toFloat() / 1024 / timePeriod

                )
            )
        } else if (listType == "vnet_tx") {
            pointsList.add(
                Point(
                    convertDateTimeToHoursSinceStart(item.dt, startDate),
                    (item.stat.vnetTx * 8) / 1024.toFloat() / 1024 / timePeriod
                )
            )
        }
    }
    return pointsList
}