package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class SshKeySingleResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: SshKeyInformationData
)

data class SshKeyInformationData(
    val id: Int,
    val name: String,
    val status: String,
    val data: String
)
