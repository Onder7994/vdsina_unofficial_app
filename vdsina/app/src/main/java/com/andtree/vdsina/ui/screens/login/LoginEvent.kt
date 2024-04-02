package com.andtree.vdsina.ui.screens.login

sealed class LoginEvent {
    data class OnEmailChange(val email: String): LoginEvent()
    data class OnPasswordChange(val password: String): LoginEvent()
    object OnLogin: LoginEvent()
    object OnShowPassword: LoginEvent()
    object OnDisablePassword: LoginEvent()
    data class OnRemember(var isRemember: Boolean): LoginEvent()
}