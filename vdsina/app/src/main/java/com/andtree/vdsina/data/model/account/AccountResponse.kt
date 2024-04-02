package com.andtree.vdsina.data.model.account

import com.google.gson.annotations.SerializedName

data class AccountResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: AccountData
)

data class AccountData(
    val account: AccountDetails,
    val created: String,
    val forecast: String,
    val can: AccountPermissions
)

data class AccountDetails(
    val id: Int,
    val name: String
)

data class AccountPermissions(
    @SerializedName("add_user")
    val addUser: Boolean,
    @SerializedName("add_service")
    val addService: Boolean,
    @SerializedName("convert_to_cash")
    val convertToCash: Boolean
)