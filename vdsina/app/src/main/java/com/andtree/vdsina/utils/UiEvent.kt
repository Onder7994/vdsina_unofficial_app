package com.andtree.vdsina.utils

sealed class UiEvent {
    data class ShowSnackBar(val message: String): UiEvent()
    data class Navigate(val route: String): UiEvent()
    data class NavigateMain(val route: String): UiEvent()
}