package com.andtree.vdsina.ui.screens.servers

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andtree.vdsina.data.model.account.AccountData
import com.andtree.vdsina.data.model.BalanceData
import com.andtree.vdsina.data.model.DeleteServerResponse
import com.andtree.vdsina.data.model.RebootServerResponse
import com.andtree.vdsina.data.model.ServersData
import com.andtree.vdsina.data.model.UpdateServerResponse
import com.andtree.vdsina.data.repository.VdsinaRepository
import com.andtree.vdsina.datastoremanager.DataStoreManager
import com.andtree.vdsina.notificationmanager.NotificationManager
import com.andtree.vdsina.ui.alertdialog.AlertDialogController
import com.andtree.vdsina.ui.alertdialog.AlertDialogEvent
import com.andtree.vdsina.utils.ApiResult
import com.andtree.vdsina.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainServersViewModel @Inject constructor(
    private val repository: VdsinaRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel(), AlertDialogController {

    var isLoading by mutableStateOf(false)
    private val _balanceData = MutableStateFlow<BalanceData?>(null)
    val balanceData = _balanceData.asStateFlow()

    private val _accountData = MutableStateFlow<AccountData?>(null)
    val accountData = _accountData.asStateFlow()

    private val _serversData = MutableStateFlow<List<ServersData>?>(null)
    val serversData = _serversData.asStateFlow()

    private val _deleteServerData = MutableStateFlow<DeleteServerResponse?>(null)

    private val _rebootServerData = MutableStateFlow<RebootServerResponse?>(null)

    private val _updateServerData = MutableStateFlow<UpdateServerResponse?>(null)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    var pendingAction by mutableStateOf("NONE")

    var selectedServerId = mutableIntStateOf(-1)

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    override var alertDialogTitle = mutableStateOf("")
        private set
    override var openAlertDialog = mutableStateOf(false)
        private set
    override var alertDialogText = mutableStateOf("")
        private set
    override var showEditText = mutableStateOf(false)
        private set
    override var isChecked = mutableStateOf(true)
        private set
    override var showChecked = mutableStateOf(false)
        private set

    var isAutoprologChecked = mutableIntStateOf(0)


    init {
        getBalanceData()
        getForecastData()
        getAllServers()
    }

    fun refreshScreen() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                getBalanceData()
                getForecastData()
                getAllServers()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    override fun onAlertDialogEvent(event: AlertDialogEvent) {
        when(event) {
            is AlertDialogEvent.onCancel -> {
                openAlertDialog.value = false
            }
            is AlertDialogEvent.onConfirm -> {
                if (pendingAction == "DELETE"){
                    deleteServerById()
                    viewModelScope.launch {
                        _deleteServerData.filterNotNull().collect { result ->
                            if (result.status == "ok"){
                                sendUiEvent(UiEvent.ShowSnackBar("Сервер удален"))
                                return@collect
                            } else {
                                sendUiEvent(UiEvent.ShowSnackBar("Ошибка при удалении сервера: ${result.statusMsg}"))
                            }
                        }
                    }
                } else if (pendingAction == "REBOOT") {
                    rebootServerById()
                    viewModelScope.launch {
                        _rebootServerData.filterNotNull().collect { result ->
                            if (result.status == "ok"){
                                sendUiEvent(UiEvent.ShowSnackBar("Сервер будет перезагружен"))
                                return@collect
                            } else {
                                sendUiEvent(UiEvent.ShowSnackBar("Ошибка при перезагруке сервера: ${result.statusMsg}"))
                            }
                        }
                    }
                } else if (pendingAction == "EDIT") {
                    updateServerInfo()
                    viewModelScope.launch {
                        _updateServerData.filterNotNull().collect { result ->
                            if (result.status == "ok"){
                                sendUiEvent(UiEvent.ShowSnackBar("Данные обновлены"))
                                return@collect
                            } else {
                                sendUiEvent(UiEvent.ShowSnackBar("Ошибка обновления данных: ${result.statusMsg}"))
                            }
                        }
                    }
                }
                openAlertDialog.value = false
                getAllServers()
            }
            is AlertDialogEvent.onTextChange -> {
                alertDialogText.value = event.text
            }
            is AlertDialogEvent.onIsChecked -> {
                isChecked.value = event.isChecked
                if (isChecked.value) {
                    isAutoprologChecked.value = 1
                } else {
                    isAutoprologChecked.value = 0
                }
            }
        }
    }

    fun onEvent(event: MainServersEvent){
        when(event){
            is MainServersEvent.OnServerClick -> {
                sendUiEvent(UiEvent.Navigate(event.route))
            }
            is MainServersEvent.OnShowDeleteDialog -> {
                selectedServerId.value = event.serverId
                openAlertDialog.value = true
                showChecked.value = false
                showEditText.value = false
                pendingAction = "DELETE"
                alertDialogTitle.value = "Удалить этот сервер?"
            }
            is MainServersEvent.OnShowRebootDialog -> {
                selectedServerId.value = event.serverId
                openAlertDialog.value = true
                showChecked.value = false
                showEditText.value = false
                pendingAction = "REBOOT"
                alertDialogTitle.value = "Перезагрузить сервер?"
            }
            is MainServersEvent.OnShowEditDialog -> {
                selectedServerId.value = event.serverId
                openAlertDialog.value = true
                pendingAction = "EDIT"
                alertDialogTitle.value = "Изменить параметры?"
                showEditText.value = true
                showChecked.value = true
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }

    private fun getBalanceData(){
        fetchData(
            dataFlow = _balanceData,
            dataRequest = { repository.getAccountBalance(dataStoreManager.tokenFLow.first()!!) },
            extractData = { response -> response.data}
        )
    }

    private fun getForecastData(){
        fetchData(
            dataFlow = _accountData,
            dataRequest = { repository.getAccountDetails(dataStoreManager.tokenFLow.first()!!) },
            extractData = { response -> response.data }
        )
    }

    private fun getAllServers() {
        fetchData(
            dataFlow = _serversData,
            dataRequest = { repository.getAllServers(dataStoreManager.tokenFLow.first()!!) },
            extractData = { response -> response.data }
        )
    }

    private fun deleteServerById() {
        fetchData(
            dataFlow = _deleteServerData,
            dataRequest = { repository.deleteServerById(dataStoreManager.tokenFLow.first()!!, selectedServerId.value) },
            extractData = { response -> response }
        )
    }

    private fun rebootServerById() {
        fetchData(
            dataFlow = _rebootServerData,
            dataRequest = { repository.rebootServerById(dataStoreManager.tokenFLow.first()!!, selectedServerId.value, "soft") },
            extractData = { response -> response }
        )
    }

    private fun updateServerInfo(){
        fetchData(
            dataFlow = _updateServerData,
            dataRequest = { repository.updateServerInfoById(dataStoreManager.tokenFLow.first()!!, alertDialogText.value, isAutoprologChecked.value, selectedServerId.value) },
            extractData = { response -> response }
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
}