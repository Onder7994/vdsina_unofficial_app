package com.andtree.vdsina.ui.screens.account

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andtree.vdsina.data.model.account.AccountLimitsData
import com.andtree.vdsina.data.model.account.AccountOperationData
import com.andtree.vdsina.data.model.account.AccountSelectedOperationData
import com.andtree.vdsina.data.repository.VdsinaRepository
import com.andtree.vdsina.datastoremanager.DataStoreManager
import com.andtree.vdsina.utils.ApiResult
import com.andtree.vdsina.utils.UiEvent
import com.andtree.vdsina.utils.convertDateToString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AccountScreenViewModel @Inject constructor(
    private val repository: VdsinaRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {

    var isLoading by mutableStateOf(false)
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    private val _accountLimitsData = MutableStateFlow<AccountLimitsData?>(null)
    val accountLimitsData = _accountLimitsData.asStateFlow()
    private val _accountOperationsData = MutableStateFlow<List<AccountOperationData>?>(null)
    val accountOperationsData = _accountOperationsData.asStateFlow()
    private val _accountSingleOperationData = MutableStateFlow<AccountSelectedOperationData?>(null)
    val accountSingleOperationData = _accountSingleOperationData.asStateFlow()


    var fromDate = mutableStateOf("")
    var toDate = mutableStateOf("")
    var operationId = mutableIntStateOf(-1)
    var showBottomSheet = mutableStateOf(false)
    var buttomSheetType = mutableStateOf("")
    var startDateForPicker: MutableState<Date?> = mutableStateOf(null)
    var endDateForPicker: MutableState<Date?> = mutableStateOf(null)

    init {
        getAccountLimits()
        getAllAccountOperations()
    }

    fun onEvent(event: AccountScreenEvent) {
        when(event){
            is AccountScreenEvent.onDateRangeClick -> {
                buttomSheetType.value = "date"
                showBottomSheet.value = true
            }
            is AccountScreenEvent.onLimitsProfileClick -> {
                buttomSheetType.value = "limits"
                showBottomSheet.value = true
            }
            is AccountScreenEvent.onSingleOperationClick -> {
                operationId.value = event.operationId
                buttomSheetType.value = "operation"
                showBottomSheet.value = true
                getSingleOperations()
            }
            is AccountScreenEvent.onFromDateChange -> {
                fromDate.value = convertDateToString(event.fromDate)
            }
            is AccountScreenEvent.onToDateChange -> {
                toDate.value = convertDateToString(event.toDate)
                showBottomSheet.value = false
                getAccountOperationsByDate()
            }
            is AccountScreenEvent.onClearClick -> {
                fromDate.value = ""
                toDate.value = ""
                startDateForPicker.value = null
                endDateForPicker.value = null
                getAllAccountOperations()
            }
        }
    }

    private fun getAccountLimits(){
        fetchData(
            dataFlow = _accountLimitsData,
            dataRequest = { repository.getAccountLimits(dataStoreManager.tokenFLow.first()!!)},
            extractData = { response -> response.data }
        )
    }

    private fun getAllAccountOperations(){
        fetchData(
            dataFlow = _accountOperationsData,
            dataRequest = { repository.getAllAccountOperations(dataStoreManager.tokenFLow.first()!!) },
            extractData = { response -> response.data }
        )
    }

    private fun getAccountOperationsByDate(){
        fetchData(
            dataFlow = _accountOperationsData,
            dataRequest = { repository.getAccountOperationsByDate(dataStoreManager.tokenFLow.first()!!, fromDate.value, toDate.value) },
            extractData = { response -> response.data }
        )
    }

    private fun getSingleOperations(){
        fetchData(
            dataFlow = _accountSingleOperationData,
            dataRequest = { repository.getAccountOperationById(dataStoreManager.tokenFLow.first()!!, operationId.value) },
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