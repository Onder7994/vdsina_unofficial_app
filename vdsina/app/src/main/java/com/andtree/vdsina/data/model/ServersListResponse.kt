package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class ServersListResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: List<ServersData>
)

data class ServersData(
    val id: Int,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val created: String,
    val updated: String,
    val end: String,
    val status: String,
    @SerializedName("status_text")
    val statusText: String,
    val ip: ServerIpData?,
    @SerializedName("server-plan")
    val serverPlan: ServerPlanData,
    val template: TemplateData,
    val datacenter: DatacenterData,
    val can: PermissionsData
)

data class ServerIpData(
    val id: Int,
    val ip: String,
    val type: String,
    val host: String,
    val gateway: String,
    val netmask: String,
    val mac: String,
    val system: Boolean,
    @SerializedName("is_net")
    val isNet: Boolean,
    val active: Boolean
)

data class ServerPlanData(
    val id: Int,
    val name: String
)

data class TemplateData(
    val id: Int,
    val name: String
)

data class DatacenterData(
    val id: Int,
    val name: String,
    val country: String
)

data class PermissionsData(
    val reboot: Boolean,
    val update: Boolean,
    val delete: Boolean,
    val prolong: Boolean
)