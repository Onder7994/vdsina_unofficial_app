package com.andtree.vdsina.di

import android.app.Application
import com.andtree.vdsina.data.network.VdsinaApi
import com.andtree.vdsina.data.repository.VdsinaRepository
import com.andtree.vdsina.datastoremanager.DataStoreManager
import com.andtree.vdsina.notificationmanager.NotificationManager
import com.andtree.vdsina.utils.Const
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(Const.VDSINA_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideVdsinaApi(retrofit: Retrofit): VdsinaApi = retrofit.create(VdsinaApi::class.java)

    @Provides
    @Singleton
    fun provideVdsinaRepository(api: VdsinaApi): VdsinaRepository = VdsinaRepository(api)

    @Provides
    @Singleton
    fun provideDataStoreManager(app: Application): DataStoreManager{
        return DataStoreManager(app)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(app: Application): NotificationManager{
        return NotificationManager(app)
    }
}