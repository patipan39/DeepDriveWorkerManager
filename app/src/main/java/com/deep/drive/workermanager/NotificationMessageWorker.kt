package com.deep.drive.workermanager

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.deep.drive.workermanager.notification.Notification
import kotlinx.coroutines.delay

class NotificationMessageWorker(
    private val context: Context,
    private val workerParams: WorkerParameters

) : CoroutineWorker(context, workerParams) {
    private val notification by lazy {
        val message = inputData.getString(Notification.KEY_MESSAGE)
        Notification.createNotification(context, message)
    }

    override suspend fun doWork(): Result {
        val worker =
            OneTimeWorkRequestBuilder<NotificationMessageWorker>()
                .setConstraints(Constraints())
                .build()
        WorkManager.getInstance(applicationContext).enqueue(worker)
        return Result.success()
    }
}