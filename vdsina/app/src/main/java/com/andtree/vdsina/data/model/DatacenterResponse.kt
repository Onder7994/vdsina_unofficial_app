package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class DatacenterResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: List<DatacenterResponseData>
)

data class DatacenterResponseData(
    val id: Int,
    val name: String,
    val country: String,
    val active: Boolean
)
