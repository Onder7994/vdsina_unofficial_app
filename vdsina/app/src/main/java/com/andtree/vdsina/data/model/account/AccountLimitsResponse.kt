package com.andtree.vdsina.data.model.account

import com.google.gson.annotations.SerializedName

data class AccountLimitsResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: AccountLimitsData
)

data class AccountLimitsData(
    val server: AccountLimitsServerData,
    @SerializedName("server-ip4")
    val serverIp4: AccountLimitsServerIp4Data,
    @SerializedName("server-ip6")
    val serverIp6: AccountLimitsServerIp6Data,
    val iso: AccountLimitsIsoData,
    val backup: AccountLimitsBackupData,
    val ssl: List<AccountLimitsSslData>,
    val domain: AccountLimitsDomainData,
    val dns: AccountLimitsDnsData,
    @SerializedName("extdisk-hdd")
    val extDiskHdd: AccountLimitsExtDiskHddData,
    @SerializedName("extdisk-nvme")
    val extDiskNvme: AccountLimitsExtDiskNvmeData,
    @SerializedName("reserve-ip")
    val reserveIp: AccountLimitsReserveIpData
)

data class AccountLimitsServerData(
    val max: Int,
    val now: Int
)

data class AccountLimitsServerIp4Data(
    val max: Int,
    @SerializedName("child_max")
    val childMax: Int,
    val now: Int
)

data class AccountLimitsServerIp6Data(
    val max: Int,
    @SerializedName("child_max")
    val childMax: Int,
    val now: Int
)

data class AccountLimitsIsoData(
    val max: Int,
    val now: Int
)

data class AccountLimitsBackupData(
    val max: Int,
    val now: Int
)

data class AccountLimitsSslData(
    val max: Int,
    val now: Int
)

data class AccountLimitsDomainData(
    val max: Int,
    val now: Int
)

data class AccountLimitsDnsData(
    val max: Int,
    val now: Int
)

data class AccountLimitsExtDiskHddData(
    val max: Int,
    val now: Int
)

data class AccountLimitsExtDiskNvmeData(
    val max: Int,
    val now: Int
)

data class AccountLimitsReserveIpData(
    val max: Int,
    val now: Int
)