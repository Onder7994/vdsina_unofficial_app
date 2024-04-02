package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class BackupResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: List<BackupResponseData>
)

data class BackupResponseData(
    val id: Int,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val created: String,
    val updated: String,
    val end: String,
    val status: String,
    @SerializedName("status_text")
    val statusText: String?,
    val server: BackupServerData?,
    val can: BackupPermissionsData
)

data class BackupServerData(
    val id: Int,
    val name: String
)

data class BackupPermissionsData(
    val update: Boolean,
    val prolong: Boolean,
    val delete: Boolean
)