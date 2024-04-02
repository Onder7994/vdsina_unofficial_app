package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class BalanceResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: BalanceData
)

data class BalanceData(
    val real: Double,
    val bonus: Double,
    val partner: Double
)