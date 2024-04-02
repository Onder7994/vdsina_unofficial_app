package com.andtree.vdsina.data.model.account

import com.google.gson.annotations.SerializedName

data class AccountSelectedOperationResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: AccountSelectedOperationData
)

data class AccountSelectedOperationData(
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
    val paylink: String?,
    val pdf: String?
)

data class AccountSelectedOperationPaymentData(
    val type: String,
    val name: String
)

data class AccountSelectedOperationServiceData(
    val id: Int,
    val name: String
)