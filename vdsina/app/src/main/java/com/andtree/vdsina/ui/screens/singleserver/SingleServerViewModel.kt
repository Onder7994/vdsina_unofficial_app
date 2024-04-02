package com.andtree.vdsina.ui.screens.singleserver

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andtree.vdsina.data.model.SingleServerResponseData
import com.andtree.vdsina.data.repository.VdsinaRepository
import com.andtree.vdsina.datastoremanager.DataStoreManager
import com.andtree.vdsina.utils.ApiResult
import com.andtree.vdsina.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleServerViewModel @Inject constructor(
    private val repository: VdsinaRepository,
    savedStateHandle: SavedStateHandle,
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    var isLoading by mutableStateOf(false)
    var serverId: Int = -1

    private val _singleServerData = MutableStateFlow<SingleServerResponseData?>(null)
    val singleServerData = _singleServerData.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        serverId = savedStateHandle.get<String>("serverId")?.toInt()!!
        getSingleServerInformation()
    }

    fun refreshScreen() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                getSingleServerInformation()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun onEvent(event: SingleServerEvent) {
        when(event){
            is SingleServerEvent.onExit -> {
                viewModelScope.launch {
                    val token = dataStoreManager.tokenFLow.first()!!
                    dataStoreManager.clearToken()
                    sendUiEvent(UiEvent.NavigateMain(event.route))
                }
            }
        }
    }

    private fun getSingleServerInformation(){
        fetchData(
            dataFlow = _singleServerData,
            dataRequest = { repository.getFullServerInformationById(dataStoreManager.tokenFLow.first()!!, serverId) },
            extractData = { response -> response.data }
        )
    }

    private fun <T : Any, R> fetchData(
        dataFlow: MutableStateFlow<R?>,
        dataRequest: suspend () -> Flow<ApiResult<T>>,
        extractData: (T) -> R
    ) {
        viewModelScope.launch {
            isLoading = true
            dataRequest().collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        dataFlow.value = extractData(result.data)
                        isLoading = false
                    }
                    is ApiResult.Error -> {
                        sendUiEvent(UiEvent.ShowSnackBar(result.errorMessage))
                        isLoading = false
                    }
                    is ApiResult.Loading -> {
                        isLoading = true
                    }
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }
}