package com.andtree.vdsina.utils

fun String.decodeUnicode(): String {
    val regex = "\\\\u([0-9A-Fa-f]{4})".toRegex()
    return regex.replace(this) { matchResult ->
        val hexCode = matchResult.groupValues[1].toInt(16)
        hexCode.toChar().toString()
    }
}