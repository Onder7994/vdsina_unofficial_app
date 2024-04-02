package com.andtree.vdsina.daterangepicker

fun String.firstLetterUppercase(): String {
    return this.replaceFirstChar { it.uppercase() }
}