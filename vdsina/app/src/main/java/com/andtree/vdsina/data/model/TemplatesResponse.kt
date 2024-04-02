package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class TemplatesResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: List<TemplatesData>
)

data class TemplatesData(
    val id: Int,
    val name: String,
    val image: String,
    val active: Boolean,
    @SerializedName("has_instruction")
    val hasInstruction: Boolean,
    @SerializedName("ssh-key")
    val sshKey: Boolean,
    @SerializedName("server-plan")
    val serverPlan: List<Int>,
    val limits: LimitsData
)

data class LimitsData(
    val cpu: ResourceLimits,
    val ram: ResourceLimits,
    val disk: ResourceLimits
)

data class ResourceLimits(
    val min: Float
)
