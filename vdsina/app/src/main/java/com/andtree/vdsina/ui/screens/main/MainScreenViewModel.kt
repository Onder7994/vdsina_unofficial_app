package com.andtree.vdsina.ui.screens.main

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andtree.vdsina.data.repository.VdsinaRepository
import com.andtree.vdsina.datastoremanager.DataStoreManager
import com.andtree.vdsina.utils.ScreenRoutes
import com.andtree.vdsina.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class
MainScreenViewModel @Inject constructor(
    private val repository: VdsinaRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {

    private val _uiEvent = Channel<UiEvent>() // передатчик
    val uiEvent = _uiEvent.receiveAsFlow() // приемник
    var showFloatingActionBottom = mutableStateOf(true)
        private set

    fun onEvent(event: MainScreenEvent) {
        when(event){
            is MainScreenEvent.Navigate -> {
                sendUiEvent(UiEvent.Navigate(event.route))
                showFloatingActionBottom.value = event.route == ScreenRoutes.MAIN_SERVERS
            }
            is MainScreenEvent.MainNavigate -> {
                sendUiEvent(UiEvent.NavigateMain(event.route))
            }
            is MainScreenEvent.OnExit -> {
                viewModelScope.launch {
                    val token = dataStoreManager.tokenFLow.first()!!
                    dataStoreManager.clearToken()
                    sendUiEvent(UiEvent.NavigateMain(event.route))
                }
            }
            is MainScreenEvent.OnNewServerClick -> {
                sendUiEvent(UiEvent.NavigateMain(ScreenRoutes.ADD_NEW_SERVER_SCREEN))
            }
        }
    }

    private fun sendUiEvent(event: UiEvent){
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}