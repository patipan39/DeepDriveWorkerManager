package com.deep.drive.workermanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.deep.drive.workermanager.notification.Notification

class NotificationSilentWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : Worker(context, workerParams) {
    private val notification by lazy {
        val message = inputData.getString(Notification.KEY_MESSAGE)
        Notification.createSilentNotification(context, message)
    }

    override fun doWork(): Result {
        Notification.showLoadingNotification(context = context, notification = notification)
        return Result.success()
    }
}