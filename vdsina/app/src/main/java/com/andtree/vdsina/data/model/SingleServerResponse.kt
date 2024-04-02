package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class SingleServerResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: SingleServerResponseData
)

data class SingleServerResponseData(
    val id: Int,
    val name: String,
    val status: String,
    @SerializedName("status_text")
    val statusText: String?,
    val created: String,
    val updated: String,
    val end: String,
    val autoprolong: Boolean,
    val ip: List<SingleServerIpData>,
    @SerializedName("ip_local")
    val ipLocal: String?,
    val host: String,
    val data: SingleServerResourceData,
    @SerializedName("server-plan")
    val serverPlan: SingleServerServerPlanData,
    @SerializedName("server-group")
    val serverGroup: SingleServerServerGroupData,
    val template: SingleServerTemplateData,
    val datacenter: SingleServerDatacenterData,
    @SerializedName("ssh-key")
    val sshKey: SingleServerSshKeyData?,
    val can: SingleServerPermissionsData,
    val bandwidth: SingleServerBandwidthData
)

data class SingleServerIpData(
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

data class SingleServerResourceData(
    val cpu: SingleServerCpuData,
    val ram: SingleServerRamData,
    val disk: SingleServerDiskData,
    val traff: SingleServerTrafficData
)

data class SingleServerCpuData(
    val type: String,
    val title: String,
    val value: Int,
    @SerializedName("for")
    val volumeType: String
)

data class SingleServerRamData(
    val type: String,
    val title: String,
    val value: Int,
    val bytes: Long,
    @SerializedName("for")
    val volumeType: String
)

data class SingleServerDiskData(
    val type: String,
    val title: String,
    val value: Int,
    val bytes: Long,
    @SerializedName("for")
    val volumeType: String
)

data class SingleServerTrafficData(
    val type: String,
    val title: String,
    val value: Int,
    val bytes: Long,
    @SerializedName("for")
    val volumeType: String
)

data class SingleServerServerPlanData(
    val id: Int,
    val name: String
)

data class SingleServerServerGroupData(
    val id: Int,
    val name: String
)

data class SingleServerTemplateData(
    val id: Int,
    val name: String,
    val image: String
)

data class SingleServerDatacenterData(
    val id: Int,
    val name: String,
    val country: String
)

data class SingleServerSshKeyData(
    val id: Int,
    val name: String,
    val status: String,
    val data: String
)

data class SingleServerPermissionsData(
    val reboot: Boolean,
    val update: Boolean,
    val delete: Boolean,
    val prolong: Boolean,
    val backup: Boolean,
    @SerializedName("ip_local")
    val ipLocal: Boolean
)

data class SingleServerBandwidthData(
    @SerializedName("current_month")
    val currentMonth: Long,
    @SerializedName("past_month")
    val pastMonth: Long
)