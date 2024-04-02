package com.andtree.vdsina.ui.screens.settings

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.extensions.isNotNull
import com.andtree.vdsina.data.repository.VdsinaRepository
import com.andtree.vdsina.datastoremanager.DataStoreManager
import com.andtree.vdsina.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val repository: VdsinaRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var isPushEnabled = mutableStateOf(true)
    var balanceTreshold = mutableDoubleStateOf(1000.0)
    var isDarkModeEnabled = mutableStateOf(false)

    init {
        viewModelScope.launch {
            dataStoreManager.enabledPushFlow.collect { isPush ->
                if (isPush != null) {
                    isPushEnabled.value = isPush
                }
            }
        }
        viewModelScope.launch {
            dataStoreManager.enabledDarkModFlow.collect { isDarkMode ->
                if (isDarkMode != null) {
                    isDarkModeEnabled.value = isDarkMode
                }
            }
        }
        viewModelScope.launch {
            dataStoreManager.balanceTresholdFlow.collect { treshold ->
                if (treshold != null) {
                    balanceTreshold.value = treshold
                }
            }
        }
    }

    fun onEvent(event: SettingsScreenEvent){
        when(event){
            is SettingsScreenEvent.onPushEnabled -> {
                isPushEnabled.value = event.isEnabled
                viewModelScope.launch {
                    dataStoreManager.savePushSetting(isPushEnabled.value)
                }
            }
            is SettingsScreenEvent.onDarkModeEnabled -> {
                isDarkModeEnabled.value = event.isEnabled
                viewModelScope.launch {
                    dataStoreManager.saveDarkModeSetting(isDarkModeEnabled.value)
                }
            }
            is SettingsScreenEvent.onBalanceTresholdChange -> {
                balanceTreshold.value = event.balance
                viewModelScope.launch {
                    dataStoreManager.saveBalanceTreshold(balanceTreshold.value)
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }
}