package com.example.kotlinmessenger.Utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class Firebaseref {
    companion object{
        lateinit var auth: FirebaseAuth
        lateinit var database: DatabaseReference
    }
}