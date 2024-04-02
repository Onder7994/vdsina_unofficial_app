package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class ServerPlanResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: List<ServerPlanResponseData>
)

data class ServerPlanResponseData(
    val id: Int,
    val name: String,
    val cost: Float,
    @SerializedName("full_coast")
    val fullCost: Float,
    val period: String,
    @SerializedName("period_name")
    val periodName: String,
    @SerializedName("min_money")
    val minMoney: Int,
    @SerializedName("can_bonus")
    val canBonus: Boolean,
    val description: String,
    val data: ServerSinglePlanData,
    @SerializedName("server-group")
    val serverGroup: Int,
    val selected: Boolean,
    val active: Boolean,
    val enable: Boolean,
    @SerializedName("has_params")
    val hasParams: Boolean,
    val backup: BackupData,
    val params: ServerPlanParamsData

)

data class ServerSinglePlanData(
    val cpu: ServerSingleResourceData,
    val ram: ServerSingleResourceData,
    val disk: ServerSingleResourceData,
    val traff: ServerSingleResourceData
)

data class ServerSingleResourceData(
    val type: String,
    val title: String,
    val value: String,
    val bytes: Long?,
    @SerializedName("for")
    val volumes: String
)

data class BackupData(
    val cost: Float,
    @SerializedName("full_cost")
    val fullCost: Float,
    val period: String,
    @SerializedName("period_name")
    val periodName: String,
    @SerializedName("for")
    val volume: String
)

data class ServerPlanParamsData(
    val cpu: ServerPlanParamsCpuData,
    val ram: ServerPlanParamsRamData,
    val disk: ServerPlanParamsDiskData,
    val ip4: ServerPlanParamsIpData
)

data class ServerPlanParamsCpuData(
    val type: String,
    val title: String,
    val min: Int,
    val max: Int,
    val step: Int,
    @SerializedName("for")
    val volumes: String,
    val cost: Int,
    @SerializedName("full_cost")
    val fullCost: Int,
    val period: String,
    @SerializedName("period_name")
    val periodName: String,
    val order: Int,
    @SerializedName("st_name")
    val stName: String,
    @SerializedName("st_bonus")
    val stBonus: Boolean
)

data class ServerPlanParamsRamData(
    val type: String,
    val title: String,
    val min: Int,
    val max: Int,
    val step: Int,
    @SerializedName("for")
    val volumes: String,
    val cost: Int,
    @SerializedName("full_cost")
    val fullCost: Int,
    val period: String,
    @SerializedName("period_name")
    val periodName: String,
    val order: Int,
    @SerializedName("st_name")
    val stName: String,
    @SerializedName("st_bonus")
    val stBonus: Boolean,
    @SerializedName("ram_for_cpu")
    val ramForCpu: Int
)

data class ServerPlanParamsDiskData(
    val type: String,
    val title: String,
    val min: Int,
    val max: Int,
    val step: Int,
    @SerializedName("for")
    val volumes: String,
    val cost: Float,
    @SerializedName("full_cost")
    val fullCost: Float,
    val period: String,
    @SerializedName("period_name")
    val periodName: String,
    val order: Int,
    @SerializedName("st_name")
    val stName: String,
    @SerializedName("st_bonus")
    val stBonus: Boolean
)

data class ServerPlanParamsIpData(
    val type: String,
    val title: String,
    val cost: Int,
    @SerializedName("full_cost")
    val fullCost: Int,
    val period: String,
    @SerializedName("period_name")
    val periodName: String,
    val order: Int,
    val hint: String,
    @SerializedName("st_id")
    val stId: Int,
    @SerializedName("st_name")
    val stName: String,
    @SerializedName("st_bonus")
    val stBonus: Boolean
)