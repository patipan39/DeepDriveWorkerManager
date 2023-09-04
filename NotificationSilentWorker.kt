package com.deep.drive.workermanager

import android.content.Context
import android.os.CountDownTimer
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.deep.drive.workermanager.notification.Notification
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.delay

class NotificationSilentWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val notification by lazy {
        val message = inputData.getString(Notification.KEY_MESSAGE)
        Notification.createSilentNotification(context, message)
    }

    override suspend fun doWork(): Result {
        setForegroundAsync(ForegroundInfo(Notification.NOTIFICATION_ID, notification))
        for (i in 0 until 100) {
            setProgressAsync(workDataOf(PROGRESS_ARG to i))
            val buildProgress = Notification.createSilentNotification(context, i.toString())
            Notification.showLoadingNotification(context, buildProgress)
            delay(DELAY)
            if (i == 99) {
                return Result.retry()
            }
        }
        return Result.success()
    }

    companion object {
        private const val DELAY = 1000L
        const val PROGRESS_ARG = "Progress"
    }
}