package com.andtree.vdsina.workermanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andtree.vdsina.data.network.VdsinaApi
import com.andtree.vdsina.data.repository.VdsinaRepository
import com.andtree.vdsina.datastoremanager.DataStoreManager
import com.andtree.vdsina.notificationmanager.NotificationManager
import com.andtree.vdsina.utils.ApiResult
import com.andtree.vdsina.utils.Const
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class BalanceCheckWorker(
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    private val api by lazy { provideRetrofit().create(VdsinaApi::class.java) }
    private val repository by lazy { VdsinaRepository(api) }
    private val dataStoreManager = DataStoreManager(context)
    private val notificationManager = NotificationManager(context)

    override suspend fun doWork(): Result {
        val token = dataStoreManager.tokenFLow.first()!!
        val balanceTreshHold = dataStoreManager.balanceTresholdFlow.first()!!
        repository.getAccountBalance(token).collect { result ->
            when(result) {
                is ApiResult.Success -> {
                    val balance = result.data.data.real
                    if (balance < balanceTreshHold) {
                        sendNotification(balance)
                    }
                }
                is ApiResult.Error -> {}
                is ApiResult.Loading -> {}
            }
        }
        return Result.success()
    }

    private fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Const.VDSINA_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun sendNotification(currentBalance: Double){
        notificationManager.createNotificationChannel(
            channelId = "com.andtree.vdsina.balance",
            channelName = "balance",
            channelDescription = "balance"

        )
        notificationManager.sendNotification(
            channelId = "com.andtree.vdsina.balance",
            title = "Низкий баланс",
            content = "Текущий баланс: $currentBalance",
            notificationId = 0
        )
    }
}