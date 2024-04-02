package com.andtree.vdsina.data.network

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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface VdsinaApi {
    @POST("v1/auth")
    suspend fun authenticate(@Body request: AuthRequest): Response<AuthResponse>
    @GET("v1/account.balance")
    suspend fun getAccountBalance(@Header("Authorization") authToken: String): Response<BalanceResponse>
    @GET("v1/account")
    suspend fun getAccountDetails(@Header("Authorization") authToken: String): Response<AccountResponse>
    @GET("v1/server")
    suspend fun getAllServers(@Header("Authorization") authToken: String): Response<ServersListResponse>
    @GET("v1/server-group")
    suspend fun getServerGroupPlans(@Header("Authorization") authToken: String): Response<ServerGroupResponse>
    @GET("v1/datacenter")
    suspend fun getDatacenters(@Header("Authorization") authToken: String): Response<DatacenterResponse>
    @GET("v1/template")
    suspend fun getOsTemplates(@Header("Authorization") authToken: String): Response<TemplatesResponse>
    @GET("v1/server-plan/{id}")
    suspend fun getServerPlanDetails(@Header("Authorization") authToken: String, @Path("id") serverPlanId: Int): Response<ServerPlanResponse>
    @GET("v1/ssh-key")
    suspend fun getSshKeyList(@Header("Authorization") authToken: String): Response<SshKeyResponse>
    @GET("v1/ssk-key/{id}")
    suspend fun getSshKeyInformation(@Header("Authorization") authToken: String, @Path("id") sshKeyId: Int): Response<SshKeySingleResponse>
    @PUT("v1/server.reboot/{id}")
    suspend fun rebootServerById(@Header("Authorization") authToken: String, @Path("id") serverId: Int, @Query("type") rebootType: String): Response<RebootServerResponse>
    @POST("v1/server")
    suspend fun createNewServer(@Body request: CreateServerRequest, @Header("Authorization") authToken: String): Response<CreateServerResponse>
    @DELETE("v1/server/{id}")
    suspend fun deleteServerById(@Header("Authorization") authToken: String, @Path("id") serverId: Int): Response<DeleteServerResponse>
    @GET("v1/iso")
    suspend fun getIsoImages(@Header("Authorization") authToken: String): Response<IsoImageResponse>
    @GET("v1/backup")
    suspend fun getBackup(@Header("Authorization") authToken: String): Response<BackupResponse>
    @PUT("v1/server/{id}")
    suspend fun updateServerInfoById(@Body request: UpdateServerRequest, @Header("Authorization") authToken: String, @Path("id") serverId: Int): Response<UpdateServerResponse>
    @GET("v1/server/{id}")
    suspend fun getFullServerInformationById(@Header("Authorization") authToken: String, @Path("id") serverId: Int): Response<SingleServerResponse>
    @GET("v1/server.stat/{id}")
    suspend fun getFullServerStats(@Header("Authorization") authToken: String, @Path("id") serverId: Int): Response<ServerStatResponse>
    @GET("v1/server.stat/{id}")
    suspend fun getServerStatsByDate(@Header("Authorization") authToken: String, @Path("id") serverId: Int, @Query("from") fromDate: String, @Query("to") toDate: String): Response<ServerStatResponse>
    @GET("v1/operation")
    suspend fun getAllAccountOperations(@Header("Authorization") authToken: String): Response<AccountOperationResponse>
    @GET("v1/operation")
    suspend fun getAccountOperationsByDate(@Header("Authorization") authToken: String, @Query("from") fromDate: String, @Query("to") toDate: String): Response<AccountOperationResponse>
    @GET("v1/operation/{id}")
    suspend fun getAccountOperationById(@Header("Authorization") authToken: String, @Path("id") operationId: Int): Response<AccountSelectedOperationResponse>
    @GET("v1/account.limit")
    suspend fun getAccountLimits(@Header("Authorization") authToken: String): Response<AccountLimitsResponse>
}