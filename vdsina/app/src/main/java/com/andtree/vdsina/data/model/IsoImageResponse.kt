package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class IsoImageResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: List<IsoImageResponseData>?
)

data class IsoImageResponseData(
    val id: Int,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val created: String,
    val updated: String,
    val end: String,
    val status: String,
    val file: IsoFileData,
    val attached: Boolean,
    val server: String?,
    val can: IsoPermissionsData
)

data class IsoFileData(
    val size: String,
    val md5: String
)

data class IsoPermissionsData(
    val delete: Boolean
)
