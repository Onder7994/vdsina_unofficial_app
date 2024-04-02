package com.andtree.vdsina.utils

sealed class ApiResult<out T : Any> {
    object Loading : ApiResult<Nothing>()
    data class Success<out T : Any>(val data: T) : ApiResult<T>()
    data class Error(val errorMessage: String) : ApiResult<Nothing>()
}