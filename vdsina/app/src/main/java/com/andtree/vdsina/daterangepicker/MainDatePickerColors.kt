package com.andtree.vdsina.daterangepicker

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class MainDatePickerColors(
    val cardColor: Color,
    val monthColor: Color,
    val iconColor: Color,
    val weekDayColor: Color,
    val dayNumberColor: Color,
    val disableDayColor: Color,
    val selectedDayNumberColor: Color,
    val selectedIndicatorColor: Color,
    val selectedDayBackgroundColor: Color,
) {
    companion object {
        @Composable
        fun defaults(
            cardColor: Color = MaterialTheme.colorScheme.surface,
            monthColor: Color = MaterialTheme.colorScheme.primary,
            iconColor: Color = MaterialTheme.colorScheme.primary,
            weekDayColor: Color = MaterialTheme.colorScheme.onSurface,
            dayNumberColor: Color = MaterialTheme.colorScheme.primary,
            disableDayColor: Color = MaterialTheme.colorScheme.secondary,
            selectedDayNumberColor: Color = MaterialTheme.colorScheme.onPrimary,
            selectedIndicatorColor: Color = MaterialTheme.colorScheme.primary,
            selectedDayBackgroundColor: Color = MaterialTheme.colorScheme.onPrimary,
        ) = MainDatePickerColors(
            cardColor = cardColor,
            monthColor = monthColor,
            iconColor = iconColor,
            weekDayColor = weekDayColor,
            dayNumberColor = dayNumberColor,
            disableDayColor = disableDayColor,
            selectedDayNumberColor = selectedDayNumberColor,
            selectedIndicatorColor = selectedIndicatorColor,
            selectedDayBackgroundColor = selectedDayBackgroundColor,
        )
    }
}
