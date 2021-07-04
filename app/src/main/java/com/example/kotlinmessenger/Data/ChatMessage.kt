package com.example.kotlinmessenger.Data

data class ChatMessage(var userId: String, var timeStamp: Long, var text: String, var messageRead: Boolean?, var notificationReceived: Boolean?)