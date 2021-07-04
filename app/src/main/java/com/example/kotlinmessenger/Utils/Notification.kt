package com.example.kotlinmessenger.Utils
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.example.kotlinmessenger.Activities.Welcome.WelcomeScreen
import com.example.kotlinmessenger.R

class Notification(var con: Context, var title: String?, var Body: String?) {
    constructor(con: Context) : this(con,null,null)
    companion object{
        var notificationID : Int = 0
    }
    val NOTIFICATION_CAHNNEL_ID = "CHANNEL_1"
    init {
        if(title != null && Body != null) {
            sendNotification(this.title!!, this.Body!!)
        }else{
            createNotificationChannel()
        }
    }
    private fun sendNotification(title: String, Body: String) {
        val intent = Intent(con, WelcomeScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(
            con, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(con,NOTIFICATION_CAHNNEL_ID)
                .setSmallIcon(R.drawable.sendicon)
                .setContentTitle(title)
                .setContentText(Body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(con)){
            notify(notificationID,notificationBuilder.build())
        }
        //notificationManager.notify(notificationID /* ID of notification */, notificationBuilder.build())
        notificationID++
    }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CAHNNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = con.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}