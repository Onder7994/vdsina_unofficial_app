package com.andtree.vdsina

import android.content.pm.PackageManager
import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import co.yml.charts.common.extensions.isNotNull
import com.andtree.vdsina.datastoremanager.DataStoreManager
import com.andtree.vdsina.ui.navigation.MainNavigationGraph
import com.andtree.vdsina.ui.theme.AppTheme
import com.andtree.vdsina.ui.theme.ThemeViewModel
import com.andtree.vdsina.ui.theme.VdsinaTheme
import com.andtree.vdsina.workermanager.BalanceCheckWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            checkPushEnabledAndScheduleWork()
        } else {
            DisablePush()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    checkPushEnabledAndScheduleWork()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            checkPushEnabledAndScheduleWork()
        }
        setContent {
            AppTheme(viewModel = themeViewModel) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigationGraph()
                }
            }
        }
    }

    private fun checkPushEnabledAndScheduleWork() {
        val dataStoreManager = DataStoreManager(this)
        lifecycleScope.launch {
            val isPushEnabled = dataStoreManager.enabledPushFlow.first()
            scheduleWork(isPushEnabled!!)
        }
    }

    private fun DisablePush(){
        val dataStoreManager = DataStoreManager(this)
        lifecycleScope.launch {
            dataStoreManager.savePushSetting(false)
        }
    }

    private fun scheduleWork(isPushEnabled: Boolean) {
        if (isPushEnabled) {
            val workRequest = PeriodicWorkRequestBuilder<BalanceCheckWorker>(1, TimeUnit.DAYS)
                .build()
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "checkBalanceWork",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}