package com.example.kotlinmessenger.Model

data class ChatMessage (var userId: String, var timeStamp: Long, var text: String, var messageRead: Boolean?, var notificationReceived: Boolean?)