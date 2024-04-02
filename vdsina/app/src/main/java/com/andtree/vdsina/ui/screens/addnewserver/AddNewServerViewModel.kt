package com.andtree.vdsina.ui.screens.addnewserver

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andtree.vdsina.data.model.BackupResponseData
import com.andtree.vdsina.data.model.BackupServerData
import com.andtree.vdsina.data.model.CreateServerResponse
import com.andtree.vdsina.data.model.CreateServerResponseData
import com.andtree.vdsina.data.model.DatacenterResponseData
import com.andtree.vdsina.data.model.IsoImageResponseData
import com.andtree.vdsina.data.model.ServerGroupData
import com.andtree.vdsina.data.model.ServerPlanResponseData
import com.andtree.vdsina.data.model.SshKeyData
import com.andtree.vdsina.data.model.TemplatesData
import com.andtree.vdsina.data.repository.VdsinaRepository
import com.andtree.vdsina.datastoremanager.DataStoreManager
import com.andtree.vdsina.ui.alertdialog.AlertDialogController
import com.andtree.vdsina.ui.alertdialog.AlertDialogEvent
import com.andtree.vdsina.utils.ApiResult
import com.andtree.vdsina.utils.ScreenRoutes
import com.andtree.vdsina.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewServerViewModel @Inject constructor(
    private val repository: VdsinaRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel(), AlertDialogController {

    var isLoading by mutableStateOf(false)
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _datacenterData = MutableStateFlow<List<DatacenterResponseData>?>(null)
    val datacenterData = _datacenterData.asStateFlow()

    private val _serverPlanData = MutableStateFlow<List<ServerPlanResponseData>?>(null)
    val serverPlanData = _serverPlanData.asStateFlow()

    private val _templateData = MutableStateFlow<List<TemplatesData>?>(null)
    val templateData = _templateData.asStateFlow()

    private val _sshKeyData = MutableStateFlow<List<SshKeyData>?>(null)
    val sshKeyData = _sshKeyData.asStateFlow()

    private val _serverGroupData = MutableStateFlow<List<ServerGroupData>?>(null)
    val serverGroupData = _serverGroupData.asStateFlow()

    private val _createServerData = MutableStateFlow<CreateServerResponse?>(null)

    private val _isoImageData = MutableStateFlow<List<IsoImageResponseData>?>(null)
    val isoImageData = _isoImageData.asStateFlow()

    private val _backupData = MutableStateFlow<List<BackupResponseData>?>(null)
    val backupData = _backupData.asStateFlow()

    val windowsOsTemplatesId = listOf(32, 18, 14, 6)

    var selectedOsTemplate = mutableIntStateOf(0)
        private set
    var minCpuFromSelectedOsTemplate = mutableFloatStateOf(0f)
        private set
    var minRamFromSelectedOsTemplate = mutableFloatStateOf(0f)
        private set
    var selectedServerGroupTemplate = mutableIntStateOf(1)
        private set
    var selectedSshKeyId = mutableIntStateOf(0)
        private set
    var selectedServerPlanTemplate = mutableIntStateOf(-1)
        private set
    var selectedDatacenter = mutableIntStateOf(-1)
        private set
    var selectedIsoImage = mutableIntStateOf(0)
        private set
    var selectedBackup = mutableIntStateOf(0)
        private set

    var serverPlanId by mutableIntStateOf(1)
    var host by mutableStateOf("")
    var name by mutableStateOf("")
    var cpu by mutableIntStateOf(1)
    var ram by mutableIntStateOf(1)
    var disk by mutableIntStateOf(5)
    var isIpChecked by mutableStateOf(true)
    var ipv4 by mutableIntStateOf(1)

    override var alertDialogTitle = mutableStateOf("")
        private set
    override var openAlertDialog = mutableStateOf(false)
        private set
    override var alertDialogText = mutableStateOf("")
        private set
    override var showEditText = mutableStateOf(false)
        private set
    override var isChecked = mutableStateOf(false)
        private set
    override var showChecked = mutableStateOf(false)
        private set

    init {
        getDatacenterData()
        getServerPlansData()
        getTemplateData()
        getSshKeyData()
        getServerGroupData()
        getIsoImage()
        getBackupData()
    }

    override fun onAlertDialogEvent(event: AlertDialogEvent) {
        when(event) {
            is AlertDialogEvent.onCancel -> {
                openAlertDialog.value = false
            }
            is AlertDialogEvent.onConfirm -> {
                if (isIpChecked){
                    ipv4 = 1
                } else {
                    ipv4 = 0
                }
                createServer()
                viewModelScope.launch {
                    _createServerData.filterNotNull().collect() { result ->
                        if (result.status == "ok"){
                            sendUiEvent(UiEvent.ShowSnackBar("Заявка на создание сервера отправлена"))
                            sendUiEvent(UiEvent.NavigateMain(ScreenRoutes.MAIN_SCREEN))
                            return@collect
                        } else {
                            sendUiEvent(UiEvent.ShowSnackBar("Ошибка при создании сервера: ${result.statusMsg}"))
                        }
                    }
                }
                openAlertDialog.value = false

            } else -> {}
        }
    }

    fun onEvent(event: AddNewServerEvent){
        when(event){
            is AddNewServerEvent.onHostNameChange -> {
                host = event.host
            }
            is AddNewServerEvent.onNameChange -> {
                name = event.name
            }
            is AddNewServerEvent.onCpuChange -> {
                cpu = event.cpu
            }
            is AddNewServerEvent.onRamChange -> {
                ram = event.ram
            }
            is AddNewServerEvent.onDiskChange -> {
                disk = event.disk
            }
            is AddNewServerEvent.onIpChange -> {
                isIpChecked = event.isIpChecked
            }
            is AddNewServerEvent.onCreationConfirm -> {
                if (selectedOsTemplate.value == 0 && selectedIsoImage.value == 0 && selectedBackup.value == 0 || selectedDatacenter.value == -1 || selectedServerPlanTemplate.value == -1) {
                    sendUiEvent(UiEvent.ShowSnackBar("Заполните обязательные поля"))
                    return
                }
                showChecked.value = false
                showEditText.value = false
                openAlertDialog.value = true
                alertDialogTitle.value = "Создать сервер?"
            }
            is AddNewServerEvent.onExit -> {
                viewModelScope.launch {
                    val token = dataStoreManager.tokenFLow.first()!!
                    dataStoreManager.clearToken()
                    sendUiEvent(UiEvent.NavigateMain(event.route))
                }
            }
            is AddNewServerEvent.onOsTemplateSelected -> {
                selectedOsTemplate.value = event.itemId
                minCpuFromSelectedOsTemplate.value = event.minCpu
                minRamFromSelectedOsTemplate.value = event.minRam
                selectedIsoImage.value = 0
                selectedBackup.value = 0
            }
            is AddNewServerEvent.onServerGroupSelected -> {
                selectedServerGroupTemplate.value = event.itemId
                serverPlanId = event.itemId
                getServerPlansData()
            }
            is AddNewServerEvent.onServerPlanSelected -> {
                selectedServerPlanTemplate.value = event.itemId
            }
            is AddNewServerEvent.onDatacenterSelected -> {
                selectedDatacenter.value = event.itemId
            }
            is AddNewServerEvent.onClearButtomClick -> {
                host = ""
                name = ""
            }
            is AddNewServerEvent.onSshKeyIdSelected -> {
                selectedSshKeyId.value = event.itemId
            }
            is AddNewServerEvent.onSshCleanClick -> {
                selectedSshKeyId.value = 0
            }
            is AddNewServerEvent.onIsoImageSelected -> {
                selectedIsoImage.value = event.itemId
                selectedOsTemplate.value = 0
            }
            is AddNewServerEvent.onBackupSelected -> {
                selectedBackup.value = event.itemId
                selectedOsTemplate.value = 0
            }
        }
    }

    private fun getDatacenterData(){
        fetchData(
            dataFlow = _datacenterData,
            dataRequest = { repository.getDatacenters(dataStoreManager.tokenFLow.first()!!) },
            extractData = { response -> response.data }
        )
    }

    private fun getServerPlansData(){
        fetchData(
            dataFlow = _serverPlanData,
            dataRequest = { repository.getServerPlanDetails(dataStoreManager.tokenFLow.first()!!, serverPlanId) },
            extractData = { response -> response.data }
        )
    }

    private fun getTemplateData(){
        fetchData(
            dataFlow = _templateData,
            dataRequest = { repository.getOsTemplates(dataStoreManager.tokenFLow.first()!!) },
            extractData = { response -> response.data }
        )
    }

    private fun getSshKeyData(){
        fetchData(
            dataFlow = _sshKeyData,
            dataRequest = { repository.getSshKeyList(dataStoreManager.tokenFLow.first()!!) },
            extractData = { response -> response.data }
        )
    }

    private fun getServerGroupData(){
        fetchData(
            dataFlow = _serverGroupData,
            dataRequest = { repository.getServerGroupPlans(dataStoreManager.tokenFLow.first()!!) },
            extractData = { response -> response.data }
        )
    }

    private fun createServer(){
        fetchData(
            dataFlow = _createServerData,
            dataRequest = { repository.createNewServer(
                dataStoreManager.tokenFLow.first()!!,
                name = name,
                autoprolong = 0,
                datacenter = selectedDatacenter.value,
                serverPlan = selectedServerPlanTemplate.value,
                template = selectedOsTemplate.value,
                sshKey = selectedSshKeyId.value,
                backup = selectedBackup.value,
                iso = selectedIsoImage.value,
                host = host,
                cpu = cpu,
                ram = ram,
                disk = disk,
                ip4 = ipv4
            ) },
            extractData = { response -> response }
        )
    }

    private fun getIsoImage(){
        fetchData(
            dataFlow = _isoImageData,
            dataRequest = { repository.getIsoImages(dataStoreManager.tokenFLow.first()!!) },
            extractData = { response -> response.data }
        )
    }

    private fun getBackupData(){
        fetchData(
            dataFlow = _backupData,
            dataRequest = { repository.getBackup(dataStoreManager.tokenFLow.first()!!) },
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