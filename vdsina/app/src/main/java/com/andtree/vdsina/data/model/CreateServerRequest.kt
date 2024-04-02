package com.andtree.vdsina.data.model

import com.google.gson.annotations.SerializedName

data class CreateServerRequest(
    val name: String?,
    val autoprolong: Int?,
    val datacenter: Int,
    @SerializedName("server-plan")
    val serverPlan: Int,
    val template: Int,
    @SerializedName("ssh-key")
    val sshKey: Int?,
    val backup: Int?,
    val iso: Int?,
    val host: String?,
    val cpu: Int?,
    val ram: Int?,
    val disk: Int?,
    val ip4: Int?
)