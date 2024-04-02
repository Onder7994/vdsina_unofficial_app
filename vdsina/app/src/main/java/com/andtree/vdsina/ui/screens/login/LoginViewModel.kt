package com.andtree.vdsina.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andtree.vdsina.data.repository.VdsinaRepository
import com.andtree.vdsina.datastoremanager.DataStoreManager
import com.andtree.vdsina.utils.ApiResult
import com.andtree.vdsina.utils.ScreenRoutes
import com.andtree.vdsina.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: VdsinaRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)

    var visibilityShowing by mutableStateOf(false)
    var isRemember by mutableStateOf(false)

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            dataStoreManager.UserEmailFlow.collect { userEmail ->
                if (!userEmail.isNullOrEmpty()) {
                    email = userEmail
                    dataStoreManager.UserPasswordFlow.collect { userPassword ->
                        if (!userPassword.isNullOrEmpty()) {
                            password = userPassword
                            isRemember = true
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: LoginEvent){
        when(event){
            is LoginEvent.OnEmailChange -> {
                email = event.email
            }
            is LoginEvent.OnPasswordChange -> {
                password = event.password
            }
            is LoginEvent.OnLogin -> {
                if (email.isEmpty() && password.isEmpty()) {
                    sendUiEvent(UiEvent.ShowSnackBar("Оба поля необходимо заполнить"))
                    return
                } else if (email.isEmpty()) {
                    sendUiEvent(UiEvent.ShowSnackBar("Поле email не может быть пустым"))
                    return
                } else if (password.isEmpty()) {
                    sendUiEvent(UiEvent.ShowSnackBar("Поле пароль не может быть пустым"))
                    return
                }
                login()
            }
            is LoginEvent.OnShowPassword -> {
                visibilityShowing = true
            }
            is LoginEvent.OnDisablePassword -> {
                visibilityShowing = false
            }
            is LoginEvent.OnRemember -> {
                isRemember = event.isRemember
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
           repository.authenticate(email, password).collect() {result ->
               when(result) {
                   is ApiResult.Loading -> {
                       isLoading = true
                   }
                   is ApiResult.Success -> {
                       if (isRemember) {
                           dataStoreManager.saveUserEmail(email)
                           dataStoreManager.saveUserPassword(password)
                       } else {
                           dataStoreManager.clearUserEmail()
                           dataStoreManager.clearUserPassword()
                       }
                       isLoading = false
                       val token = result.data.data.token
                       dataStoreManager.saveToken(token)
                       sendUiEvent(UiEvent.Navigate(ScreenRoutes.MAIN_SCREEN))
                   }
                   is ApiResult.Error -> {
                       isLoading = false
                       sendUiEvent(UiEvent.ShowSnackBar(result.errorMessage))
                   }
               }
           }
        }
    }
    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }
}