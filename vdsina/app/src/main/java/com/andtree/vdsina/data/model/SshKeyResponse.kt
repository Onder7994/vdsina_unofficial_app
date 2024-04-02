package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class SshKeyResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: List<SshKeyData>
)

data class SshKeyData(
    val id: Int,
    val name: String
)
