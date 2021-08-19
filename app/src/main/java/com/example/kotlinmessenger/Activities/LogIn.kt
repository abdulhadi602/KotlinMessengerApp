package com.example.kotlinmessenger.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity

import com.example.kotlinmessenger.Activities.Welcome.WelcomeScreen
import com.example.kotlinmessenger.Model.IDs
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.Utils.DisplayToast
import com.example.kotlinmessenger.Utils.Notification
import com.example.kotlinmessenger.Utils.Position

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_log_in.*


class LogIn : AppCompatActivity() {
lateinit var scale_Down : Animation
    lateinit var scale_Up : Animation
    lateinit var rotate : Animation
    lateinit var rotate_circlular : Animation


    lateinit var wobble_one : Animation
    lateinit var wobble_two : Animation
    lateinit var wobble_three : Animation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


        LogOn.setOnClickListener(LoginListener)
        scale_Down = AnimationUtils.loadAnimation(this, R.anim.scale_down_x)
        scale_Down.setAnimationListener(scale_downX_Listener)
        scale_Up = AnimationUtils.loadAnimation(this, R.anim.scale_up_x)
        scale_Up.setAnimationListener(scale_upX_Listener)
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise)
        rotate.setInterpolator(LinearInterpolator())
        rotate.setAnimationListener(rotate_Listener)
        rotate_circlular = AnimationUtils.loadAnimation(this, R.anim.rotation_animation)

        wobble_one = AnimationUtils.loadAnimation(this, R.anim.wobble1)


        fade_left.setOnClickListener {
            fade_left.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_translate_left))
        }
        fade_right.setOnClickListener {
            fade_right.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.fade_translate_right
                )
            )
        }
        fade_up.setOnClickListener {
            fade_up.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_translate_up))
        }
        fade_down.setOnClickListener {
            fade_down.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_translate_down))
        }
        Wobble.setOnClickListener {
            Wobble.startAnimation(wobble_one)
        }









    }


    var rotate_Listener = (object : Animation.AnimationListener{
        override fun onAnimationStart(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            animation_img.startAnimation(scale_Up)
        }

        override fun onAnimationRepeat(animation: Animation?) {

        }

    })
    var scale_downX_Listener = (object : Animation.AnimationListener{
        override fun onAnimationStart(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            animation_img.startAnimation(rotate)

        }

        override fun onAnimationRepeat(animation: Animation?) {

        }

    })
    var scale_upX_Listener = (object : Animation.AnimationListener{
        override fun onAnimationStart(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            animation_img.startAnimation(scale_Down)
        }

        override fun onAnimationRepeat(animation: Animation?) {

        }

    })

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
        }else{

                animation_img.startAnimation(scale_Down)

           // return@OnClickListener

        }
        Log.d("TAG", "${UsernameOrEmail.text}")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            UsernameOrEmail.text.toString(),
            Password.text.toString()
        )
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    Log.d("TAG", "signInWithEmail:success")
                    val user = FirebaseAuth.getInstance().currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    DisplayToast(
                        task.exception?.localizedMessage.toString(), false,
                        Position.TOP, this
                    )
                    updateUI(null)
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        if(user != null){
            val intent = Intent(this@LogIn, WelcomeScreen::class.java)

            IDs.UserId = user.uid
            IDs.sharedPreferences = this?.getPreferences(Context.MODE_PRIVATE)

            with(IDs.sharedPreferences.edit()){
                putString("UserId", "${IDs.UserId}")
                apply()
            }


            Notification(this@LogIn)
            startActivity(intent)
        }
       /** val user = FirebaseAuth.getInstance().currentUser
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
        }**/
    }
}
