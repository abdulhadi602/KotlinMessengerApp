package com.example.kotlinmessenger.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Display
import android.view.View

import com.example.kotlinmessenger.Activities.Welcome.WelcomeScreen
import com.example.kotlinmessenger.Data.IDs
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.Utils.DisplayToast
import com.example.kotlinmessenger.Utils.Notification
import com.example.kotlinmessenger.Utils.Position
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_log_in.Password

class LogIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        LogOn.setOnClickListener(LoginListener)
    }
    private fun isFieldEmpty() : Boolean{
        var empty = false
        if(TextUtils.isEmpty(UsernameOrEmail.text)) {
            UsernameOrEmail.setError("Please enter email")
            empty = true
        }else if (TextUtils.isEmpty(Password.text)){
            Password.setError("Please enter password")
            empty = true
        }else{
            empty = false
        }
        return empty
    }


    var LoginListener = View.OnClickListener {
        if(isFieldEmpty()){
            return@OnClickListener
        }
        Log.d("TAG", "${UsernameOrEmail.text}")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(UsernameOrEmail.text.toString(), Password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    Log.d("TAG", "signInWithEmail:success")
                    val user = FirebaseAuth.getInstance().currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    DisplayToast(task.exception?.localizedMessage.toString(), false,
                        Position.TOP,this)
                    updateUI(null)
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        if(user != null){
            val intent = Intent(this@LogIn , WelcomeScreen:: class.java)
            IDs.UserId = user.uid
            val sharedPref = this?.getPreferences(Context.MODE_PRIVATE)
            IDs.sharedPreferences = sharedPref
            with(sharedPref.edit()){
                putString("UserId","${IDs.UserId}")
                apply()
            }


            Notification(this@LogIn)
            startActivity(intent)
        }
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid
        }
    }
}
