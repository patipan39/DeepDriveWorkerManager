package com.deep.drive.workermanager

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.deep.drive.workermanager.notification.Notification
import com.deep.drive.workermanager.ui.theme.DeepDriveWorkerManagerTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeepDriveWorkerManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
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
                            ).buildWithConstraint("15 นาทีแล้ว !!!!").build()
                            WorkManager.getInstance(applicationContext)
                                .enqueueUniquePeriodicWork(
                                    "PeriodicWork",
                                    ExistingPeriodicWorkPolicy.UPDATE,
                                    worker
                                )
                        }) {
                            Text(text = "PeriodicWork")
                        }

                        Button(onClick = {
                            val countDownTimer = object : CountDownTimer(30000, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    val worker =
                                        OneTimeWorkRequestBuilder<NotificationSilentWorker>()
                                            .buildWithConstraint(millisUntilFinished.toString())
                                            .build()
                                    WorkManager.getInstance(applicationContext)
                                        .enqueue(worker)
                                }

                                override fun onFinish() {
                                    val worker =
                                        OneTimeWorkRequestBuilder<NotificationSilentWorker>()
                                            .buildWithConstraint("หมดเวลา !!!!!").build()
                                    WorkManager.getInstance(applicationContext)
                                        .enqueue(worker)
                                }
                            }
                            countDownTimer.start()
                        }) {
                            Text(text = "Show Clock")
                        }
                    }
                }
            }
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