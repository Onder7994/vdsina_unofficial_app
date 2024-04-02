package com.andtree.vdsina.data.model

data class CreateServerErrorResponse(
    val status: String,
    val statusCode: Int,
    val statusMsg: String,
    val description: String?,
    val data: Map<String, List<String>>?
)
