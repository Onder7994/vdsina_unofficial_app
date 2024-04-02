package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class UpdateServerResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: Unit
)
