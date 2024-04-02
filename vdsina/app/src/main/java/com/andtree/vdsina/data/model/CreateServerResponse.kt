package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class CreateServerResponse(
    val status: String,
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("status_msg")
    val statusMsg: String,
    val description: String?,
    val data: CreateServerResponseData?
)

data class CreateServerResponseData(
    val id: Int?,
    @SerializedName("server-plan")
    val serverPlan: List<String>?
)