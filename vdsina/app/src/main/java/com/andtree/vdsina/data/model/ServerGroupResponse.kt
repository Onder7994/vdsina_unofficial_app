package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class ServerGroupResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: List<ServerGroupData>
)

data class ServerGroupData(
    val id: Int,
    val name: String,
    val type: String,
    val image: String,
    val active: Boolean,
    val description: String
)