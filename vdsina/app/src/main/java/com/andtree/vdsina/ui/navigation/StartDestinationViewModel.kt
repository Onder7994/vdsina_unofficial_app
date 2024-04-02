package com.andtree.vdsina.ui.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andtree.vdsina.datastoremanager.DataStoreManager
import com.andtree.vdsina.utils.ScreenRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StartDestinationViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    var startDestination = mutableStateOf(ScreenRoutes.LOGIN_SCREEN)
    var isLoading = mutableStateOf(true)

    init {
        viewModelScope.launch {
            val token = dataStoreManager.tokenFLow.first()
            startDestination.value = if (token.isNullOrEmpty()) {
                ScreenRoutes.LOGIN_SCREEN
            } else {
                ScreenRoutes.MAIN_SCREEN
            }
            isLoading.value = false
        }
    }
}