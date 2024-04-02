package com.andtree.vdsina.data.model.serverstat

import com.google.gson.annotations.SerializedName

data class ServerStatResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: List<ServerListStatData>
)

data class ServerListStatData(
    val dt: String,
    val stat: ServerStatData
)

data class ServerStatData(
    val cpu: Float,
    @SerializedName("disk_reads")
    val diskReads: Int,
    @SerializedName("disk_writes")
    val diskWrites: Long,
    @SerializedName("lnet_rx")
    val lnetRx: Int,
    @SerializedName("lnet_tx")
    val lnetTx: Int,
    @SerializedName("vnet_rx")
    val vnetRx: Long,
    @SerializedName("vnet_tx")
    val vnetTx: Long,
    @SerializedName("vnet_packets_rx")
    val vnetPacketsRx: Long,
    @SerializedName("vnet_packets_tx")
    val vnetPacketsTx: Long
)