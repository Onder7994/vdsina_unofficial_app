package com.andtree.vdsina.utils

import com.andtree.vdsina.data.model.CreateServerErrorResponse
import com.google.gson.Gson

fun extractDataFromErrorJson(errorJson: String?): Map<String, List<String>>? {
    val decodedJson = errorJson?.decodeUnicode()
    val errorResponse = Gson().fromJson(decodedJson, CreateServerErrorResponse::class.java)
    return errorResponse.data
}