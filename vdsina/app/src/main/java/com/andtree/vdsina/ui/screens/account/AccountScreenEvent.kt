package com.andtree.vdsina.ui.screens.account

import java.util.Date

sealed class AccountScreenEvent {
    object onDateRangeClick: AccountScreenEvent()
    data class onSingleOperationClick(val operationId: Int): AccountScreenEvent()
    object onLimitsProfileClick: AccountScreenEvent()
    data class onFromDateChange(val fromDate: Date): AccountScreenEvent()
    data class onToDateChange(val toDate: Date): AccountScreenEvent()
    object onClearClick: AccountScreenEvent()
}