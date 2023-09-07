package com.deep.drive.workermanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.deep.drive.workermanager.notification.Notification
import com.deep.drive.workermanager.ui.theme.DeepDriveWorkerManagerTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private var data: ProgressData by mutableStateOf(ProgressData(0))

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        setContent {
            DeepDriveWorkerManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            val worker =
                                OneTimeWorkRequestBuilder<NotificationMessageWorker>()
                                    .buildWithConstraint("แสดงครั้งเดียวนะ")
                                    .build()
                            WorkManager.getInstance(applicationContext).enqueue(worker)
                        }) {
                            Text(text = "One Time Short")
                        }

                        Button(onClick = {
                            //minimum 15 minute
                            val worker = PeriodicWorkRequestBuilder<NotificationMessageWorker>(
                                15,
                                TimeUnit.MINUTES
                            ).buildWithConstraint("เริ่ม นับถอยหลัง 15 นาที").build()
                            WorkManager.getInstance(applicationContext)
                                .enqueueUniquePeriodicWork(
                                    "PeriodicWork",
                                    ExistingPeriodicWorkPolicy.UPDATE,
                                    worker
                                )
                        }) {
                            Text(text = "PeriodicWork")
                        }

                        ShowProgressButton(data.progress)

                        Button(onClick = {
                            //chain async worker
                            val worker1 =
                                OneTimeWorkRequestBuilder<NotificationMessageWorker>()
                                    .buildWithConstraint("แสดงครั้งเดียวนะ")
                                    .build()

                            val worker2 =
                                OneTimeWorkRequestBuilder<NotificationMessageWorker>()
                                    .buildWithConstraint("แสดงครั้งเดียวนะ")
                                    .build()

                            val worker3 = OneTimeWorkRequestBuilder<NotificationSilentWorker>()
                                .buildWithConstraint("เริ่ม นับถอยหลัง 15 นาที")
                                .build()

                            WorkManager.getInstance(applicationContext)
                                .beginWith(listOf(worker1))
                                .enqueue()
                        }) {
                            Text(text = "Chain Parallel")
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ShowProgressButton(progress: Int) {
        Button(onClick = {
            val manager = WorkManager.getInstance(applicationContext)
            val isDone = manager.getWorkInfosByTag("ShowClock").isDone

            if (!isDone) {
                manager.getWorkInfosByTag("ShowClock").cancel(true)
            }

            val worker = OneTimeWorkRequestBuilder<NotificationSilentWorker>()
                .buildWithConstraint("อัพเดทแล้วนะ")
                .build()
            WorkManager.getInstance(applicationContext)
                .enqueueUniqueWork(
                    "ShowClock",
                    ExistingWorkPolicy.REPLACE,
                    worker
                )
        }) {
            Text(text = "$progress")
        }
    }

    private fun initViewModel() {
        WorkManager.getInstance(applicationContext)
            .getWorkInfosLiveData(WorkQuery.fromUniqueWorkNames("ShowClock")).observe(this) {
                val workerProgress =
                    it.firstOrNull()?.progress?.getInt(NotificationSilentWorker.PROGRESS_ARG, 0)
                data = ProgressData(workerProgress ?: 0)
            }
    }

    private fun PeriodicWorkRequest.Builder.buildWithConstraint(message: String): PeriodicWorkRequest.Builder {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        setConstraints(constraints)
        val data = Data.Builder().apply {
            putString(Notification.KEY_MESSAGE, message)
        }
        setInputData(data.build())
        return this
    }

    private fun OneTimeWorkRequest.Builder.buildWithConstraint(message: String): OneTimeWorkRequest.Builder {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        setConstraints(constraints)
        val data = Data.Builder().apply {
            putString(Notification.KEY_MESSAGE, message)
        }
        setInputData(data.build())
        return this
    }
}

class ProgressData(val progress: Int)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DeepDriveWorkerManagerTheme {
        Greeting("Android")
    }
}