package com.deep.drive.workermanager.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.deep.drive.workermanager.R

class Notification {
    companion object {
        private const val NOTIFICATION_ID = 1000
        private const val CHANNEL_ID = "ShowLoading"
        const val KEY_MESSAGE = "message"

        fun createNotification(context: Context, message: String?): Notification {
            val notifyPendingIntent = PendingIntent.getActivity(
                context, 0, Intent(), PendingIntent.FLAG_UPDATE_CURRENT
            )

            return NotificationCompat.Builder(context, CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.purple_200))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentIntent(notifyPendingIntent)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        }

        fun createSilentNotification(context: Context, message: String?): Notification {
            val notifyPendingIntent = PendingIntent.getActivity(
                context, 0, Intent(), PendingIntent.FLAG_UPDATE_CURRENT
            )
            val remoteView = RemoteViews(context.packageName, R.layout.custom_layout_notification)
            remoteView.setTextViewText(R.id.timerMessage, message)
            return NotificationCompat.Builder(context, CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.purple_200))
                .setContentIntent(notifyPendingIntent)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        }

        @SuppressLint("MissingPermission")
        fun showLoadingNotification(
            context: Context,
            notification: Notification
        ) {
            val notificationManager = NotificationManagerCompat.from(context)
            with(notificationManager) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel =
                        NotificationChannel(
                            CHANNEL_ID,
                            "Loading",
                            NotificationManager.IMPORTANCE_HIGH
                        )
                    createNotificationChannel(channel)
                }
                notify(NOTIFICATION_ID, notification)
            }
        }
    }
}