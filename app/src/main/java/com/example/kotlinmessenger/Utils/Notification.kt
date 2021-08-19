package com.example.kotlinmessenger.Utils
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.kotlinmessenger.Services.NotificationService

class Notification(var con: Context)  {


    companion object{
        var notificationID : Int = 0
        lateinit var con : Context
        lateinit var notificationManager: NotificationManager
        val NOTIFICATION_CAHNNEL_ID = "CHANNEL_1"
    }

    init {
        this.con = con
            createNotificationChannel()
            Firebaseref.init()
         con.startForegroundService(Intent(con.applicationContext, NotificationService::class.java))


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
            notificationManager = con.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }



}