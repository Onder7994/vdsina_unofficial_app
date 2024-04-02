package com.andtree.vdsina.datastoremanager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val DATA_STORE_NAME = "datastore"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATA_STORE_NAME)

class DataStoreManager(val context: Context) {
    companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PASSWORD = stringPreferencesKey("user_password")
        val ENABLED_PUSH = booleanPreferencesKey("enabled_push")
        val ENABLED_DARKMODE = booleanPreferencesKey("enabled_darmod")
        val BALANCE_TRESHOLD = doublePreferencesKey("balance_treshold")
    }

    val tokenFLow: Flow<String?> = context.dataStore.data.map { preference ->
        preference[TOKEN_KEY]
    }

    val UserEmailFlow: Flow<String?> = context.dataStore.data.map { preference ->
        preference[USER_EMAIL]
    }

    val UserPasswordFlow: Flow<String?> = context.dataStore.data.map { preference ->
        preference[USER_PASSWORD]
    }

    val enabledPushFlow: Flow<Boolean?> = context.dataStore.data.map { preference ->
        preference[ENABLED_PUSH] ?: true
    }

    val enabledDarkModFlow: Flow<Boolean?> = context.dataStore.data.map { preference ->
        preference[ENABLED_DARKMODE] ?: false
    }

    val balanceTresholdFlow: Flow<Double?> = context.dataStore.data.map { preference ->
        preference[BALANCE_TRESHOLD]
    }

    suspend fun saveBalanceTreshold(balanceTreshold: Double) {
        context.dataStore.edit { preference ->
            preference[BALANCE_TRESHOLD] = balanceTreshold
        }
    }

    suspend fun savePushSetting(isPushEnabled: Boolean) {
        context.dataStore.edit { preference ->
            preference[ENABLED_PUSH] = isPushEnabled
        }
    }

    suspend fun saveDarkModeSetting(isDarkModeEnabled: Boolean) {
        context.dataStore.edit { preference ->
            preference[ENABLED_DARKMODE] = isDarkModeEnabled
        }
    }

    suspend fun saveUserEmail(userEmail: String) {
        context.dataStore.edit { preference ->
            preference[USER_EMAIL] = userEmail
        }
    }

    suspend fun saveUserPassword(userPassword: String) {
        context.dataStore.edit { preference ->
            preference[USER_PASSWORD] = userPassword
        }
    }

    suspend fun saveToken(token: String){
        context.dataStore.edit { preference ->
            preference[TOKEN_KEY] = token
        }
    }

    suspend fun clearToken(){
        context.dataStore.edit { preference ->
            preference.remove(TOKEN_KEY)
        }
    }

    suspend fun clearUserEmail() {
        context.dataStore.edit { preference ->
            preference.remove(USER_EMAIL)
        }
    }

    suspend fun clearUserPassword() {
        context.dataStore.edit { preference ->
            preference.remove(USER_PASSWORD)
        }
    }
}