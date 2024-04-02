package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class DeleteServerResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val description: String?,
    val data: String
)
