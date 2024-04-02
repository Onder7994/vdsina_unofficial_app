package com.andtree.vdsina.data.model.auth

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val status: String,
    @SerializedName("status_msg")
    val statusMsg: String,
    val data: TokenData
)

data class TokenData(
    val token: String
)