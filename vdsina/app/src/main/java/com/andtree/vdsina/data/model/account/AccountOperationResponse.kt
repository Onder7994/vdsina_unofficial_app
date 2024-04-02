package com.andtree.vdsina.data.model.account

import com.google.gson.annotations.SerializedName

data class AccountOperationResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: List<AccountOperationData>
)

data class AccountOperationData(
    val id: Int,
    val purse: String,
    val type: Int,
    val status: Int,
    val summ: Float,
    val created: String,
    val updated: String,
    val comment: String,
    val payment: AccountSelectedOperationPaymentData?,
    val service: AccountSelectedOperationServiceData?,
    val paylink: String?
)
