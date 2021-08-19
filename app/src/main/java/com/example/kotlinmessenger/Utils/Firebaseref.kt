package com.example.kotlinmessenger.Utils

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Firebaseref {
    companion object{
        lateinit var auth: FirebaseAuth
        lateinit var database: DatabaseReference
        fun init(){
            //mPushHandler = iPushHandler;

            auth = FirebaseAuth.getInstance()
            database = FirebaseDatabase.getInstance("https://kotlinmessenger-74ae0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
        }
    }

}