package com.andtree.vdsina.data.repository

import com.andtree.vdsina.data.model.account.AccountResponse
import com.andtree.vdsina.data.model.auth.AuthRequest
import com.andtree.vdsina.data.model.auth.AuthResponse
import com.andtree.vdsina.data.model.BackupResponse
import com.andtree.vdsina.data.model.BalanceResponse
import com.andtree.vdsina.data.model.CreateServerRequest
import com.andtree.vdsina.data.model.CreateServerResponse
import com.andtree.vdsina.data.model.DatacenterResponse
import com.andtree.vdsina.data.model.DeleteServerResponse
import com.andtree.vdsina.data.model.IsoImageResponse
import com.andtree.vdsina.data.model.RebootServerResponse
import com.andtree.vdsina.data.model.ServerGroupResponse
import com.andtree.vdsina.data.model.ServerPlanResponse
import com.andtree.vdsina.data.model.ServersListResponse
import com.andtree.vdsina.data.model.SingleServerResponse
import com.andtree.vdsina.data.model.SshKeyResponse
import com.andtree.vdsina.data.model.SshKeySingleResponse
import com.andtree.vdsina.data.model.TemplatesResponse
import com.andtree.vdsina.data.model.UpdateServerRequest
import com.andtree.vdsina.data.model.UpdateServerResponse
import com.andtree.vdsina.data.model.account.AccountLimitsResponse
import com.andtree.vdsina.data.model.account.AccountOperationResponse
import com.andtree.vdsina.data.model.account.AccountSelectedOperationResponse
import com.andtree.vdsina.data.model.serverstat.ServerStatResponse
import com.andtree.vdsina.data.network.VdsinaApi
import com.andtree.vdsina.utils.ApiResult
import com.andtree.vdsina.utils.decodeUnicode
import com.andtree.vdsina.utils.extractDataFromErrorJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class VdsinaRepository(private val api: VdsinaApi) {
    private inline fun <T : Any> makeApiRequest(crossinline requestCall: suspend () -> Response<T>): Flow<ApiResult<T>> = flow {
        emit(ApiResult.Loading)
        var errorMessage: String
        try {
            val response = requestCall()
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResult.Success(response.body()!!))
            } else {
                val errorBody = response.errorBody()?.string()?.decodeUnicode()
                errorMessage = when (response.code()) {
                    400 -> "Данные переданы неверно или запрос сформирован неправильно"
                    401 -> "Требуется аутентификация"
                    403 -> "Доступ запрещен"
                    500 -> "Внутренняя ошибка сервера"
                    else -> "Ошибка: ${response.code()}"
                }
                if (errorBody != null) {
                    val errorBodySerilization = extractDataFromErrorJson(errorBody)
                    errorMessage = "$errorMessage. Детали: $errorBodySerilization"
                } else {
                    errorMessage = errorMessage
                }
                emit(ApiResult.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Неизвестная ошибка"))
        }
    }

    suspend fun authenticate(email: String, password: String): Flow<ApiResult<AuthResponse>> {
        val authRequest = AuthRequest(email, password)
        return makeApiRequest { api.authenticate(authRequest) }
    }

    suspend fun getAccountBalance(authToken: String): Flow<ApiResult<BalanceResponse>> {
        return makeApiRequest{ api.getAccountBalance(authToken) }
    }

    suspend fun getAccountDetails(authToken: String): Flow<ApiResult<AccountResponse>> {
        return makeApiRequest{ api.getAccountDetails(authToken) }
    }

    suspend fun getAllServers(authToken: String): Flow<ApiResult<ServersListResponse>> {
        return makeApiRequest{ api.getAllServers(authToken) }
    }

    suspend fun getServerGroupPlans(authToken: String): Flow<ApiResult<ServerGroupResponse>> {
        return makeApiRequest{ api.getServerGroupPlans(authToken) }
    }

    suspend fun getDatacenters(authToken: String): Flow<ApiResult<DatacenterResponse>> {
        return makeApiRequest{ api.getDatacenters(authToken) }
    }

    suspend fun getOsTemplates(authToken: String): Flow<ApiResult<TemplatesResponse>> {
        return makeApiRequest{ api.getOsTemplates(authToken) }
    }

    suspend fun getServerPlanDetails(authToken: String, serverPlanId: Int): Flow<ApiResult<ServerPlanResponse>> {
        return makeApiRequest{ api.getServerPlanDetails(authToken, serverPlanId) }
    }

    suspend fun getSshKeyList(authToken: String): Flow<ApiResult<SshKeyResponse>> {
        return makeApiRequest{ api.getSshKeyList(authToken) }
    }

    suspend fun getSshKeyInformation(authToken: String, sshKeyId: Int): Flow<ApiResult<SshKeySingleResponse>> {
        return makeApiRequest{ api.getSshKeyInformation(authToken, sshKeyId) }
    }

    suspend fun createNewServer(
        authToken: String, name: String?, autoprolong: Int?, datacenter: Int, serverPlan: Int,
        template: Int, sshKey: Int?, backup: Int?, iso: Int?, host: String?, cpu: Int?, ram: Int?,
        disk: Int?, ip4: Int?
    ): Flow<ApiResult<CreateServerResponse>> {
        val createServerRequest = CreateServerRequest(
            name,
            autoprolong,
            datacenter,
            serverPlan,
            template,
            sshKey,
            backup,
            iso,
            host,
            cpu,
            ram,
            disk,
            ip4
        )
        return makeApiRequest { api.createNewServer(createServerRequest, authToken) }
    }

    suspend fun deleteServerById(authToken: String, serverId: Int): Flow<ApiResult<DeleteServerResponse>> {
        return makeApiRequest { api.deleteServerById(authToken, serverId) }
    }

    suspend fun rebootServerById(authToken: String, serverId: Int, rebootType: String): Flow<ApiResult<RebootServerResponse>> {
        return makeApiRequest { api.rebootServerById(authToken, serverId, rebootType) }
    }

    suspend fun getIsoImages(authToken: String): Flow<ApiResult<IsoImageResponse>> {
        return makeApiRequest { api.getIsoImages(authToken) }
    }

    suspend fun getBackup(authToken: String): Flow<ApiResult<BackupResponse>> {
        return makeApiRequest { api.getBackup(authToken) }
    }

    suspend fun updateServerInfoById(authToken: String, name: String?, autoprolong: Int?, serverId: Int): Flow<ApiResult<UpdateServerResponse>> {
        val updateServerRequest = UpdateServerRequest(
            name,
            autoprolong
        )
        return makeApiRequest { api.updateServerInfoById(updateServerRequest, authToken, serverId) }
    }

    suspend fun getFullServerInformationById(authToken: String, serverId: Int): Flow<ApiResult<SingleServerResponse>> {
        return makeApiRequest { api.getFullServerInformationById(authToken, serverId) }
    }

    suspend fun getFullServerStats(authToken: String, serverId: Int): Flow<ApiResult<ServerStatResponse>> {
        return makeApiRequest { api.getFullServerStats(authToken, serverId) }
    }

    suspend fun getServerStatsByDate(authToken: String, serverId: Int, fromDate: String, toDate: String): Flow<ApiResult<ServerStatResponse>> {
        return makeApiRequest { api.getServerStatsByDate(authToken, serverId, fromDate, toDate) }
    }

    suspend fun getAllAccountOperations(authToken: String): Flow<ApiResult<AccountOperationResponse>> {
        return makeApiRequest { api.getAllAccountOperations(authToken) }
    }

    suspend fun getAccountOperationsByDate(authToken: String, fromDate: String, toDate: String): Flow<ApiResult<AccountOperationResponse>> {
        return makeApiRequest { api.getAccountOperationsByDate(authToken, fromDate, toDate) }
    }

    suspend fun getAccountOperationById(authToken: String, operationId: Int): Flow<ApiResult<AccountSelectedOperationResponse>> {
        return makeApiRequest { api.getAccountOperationById(authToken, operationId) }
    }

    suspend fun getAccountLimits(authToken: String): Flow<ApiResult<AccountLimitsResponse>> {
        return makeApiRequest { api.getAccountLimits(authToken) }
    }
}