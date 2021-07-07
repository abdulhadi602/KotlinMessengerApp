package com.example.kotlinmessenger.Activities


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.Activities.Welcome.WelcomeScreen
import com.example.kotlinmessenger.Data.IDs
import com.example.kotlinmessenger.Data.UserData
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.Utils.DisplayToast
import com.example.kotlinmessenger.Utils.Firebaseref
import com.example.kotlinmessenger.Utils.Notification
import com.example.kotlinmessenger.Utils.Position
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.Password
import java.util.*
import kotlin.collections.HashMap


class LogOn : AppCompatActivity() {
    private val pickImage = 100
    private var imageUri: Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        Register.setOnClickListener(RegisterListener)
        LoginScreen.setOnClickListener(LogOnScreenListener)
        Firebaseref.init()
        var startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
             if(it.resultCode == Activity.RESULT_OK){
                 imageUri = it.data?.data
                 UserImg.setImageURI(it.data?.data)
             }
        }
        UserImg.setOnClickListener(View.OnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startForResult.launch(intent)

        })

    }
    private fun isFieldEmpty() : Boolean{
        var empty = false
        if(TextUtils.isEmpty(Email.text)) {
            Email.setError("Please enter email")
            empty = true
        }else if (TextUtils.isEmpty(Password.text)){
            Password.setError("Please enter password")
            empty = true
        }else if(TextUtils.isEmpty(Username.text)){
            Username.setError("Please enter username")
            empty = true
        }else if(imageUri == null){

            DisplayToast("Please select an image",true,Position.TOP,this)

            empty = true
        }
        else{
            empty = false
        }
        return empty
    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = Firebaseref.auth.currentUser
        if(currentUser != null){
            reload()
        }
    }
    var RegisterListener : View.OnClickListener  = View.OnClickListener {
        if(isFieldEmpty()){
            return@OnClickListener
        }
        Firebaseref.auth.createUserWithEmailAndPassword(Email.text.toString(), Password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = Firebaseref.auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.

                    Log.w("TAG", "createUserWithEmail:failure", task.exception)


                    DisplayToast(task.exception?.localizedMessage.toString(), false,Position.TOP,this)
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        Notification(this@LogOn)
        if(user!=null) {
            if (imageUri == null) {
                val intent = Intent(this@LogOn , WelcomeScreen:: class.java)
                startActivity(intent)
            }
            else {
                var filename = UUID.randomUUID().toString()
                val username = Username.text.toString()
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .setPhotoUri(Uri.parse("/images/$filename"))
                    .build()

                Firebaseref.auth.currentUser!!.updateProfile(profileUpdate).addOnSuccessListener() {

                    DisplayToast("Profile Updated with image", false, Position.TOP, this)
                    val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
                    ref.putFile(imageUri!!).addOnSuccessListener {
                        Log.d("TAG", "Image uploaded")
                        writeNewUser(
                            profileUpdate.displayName!!,
                            Firebaseref.auth.currentUser!!.email!!,
                            profileUpdate.photoUri.toString()
                        )
                    }


                }.addOnFailureListener {
                    Log.d("TAG", "Failed to save image and username $it")
                }

            }

        }else{
            Username.setText("")
            Email.setText("")
            Password.setText("")
            UserImg.setImageResource(R.drawable.ic_launcher_background)
        }
    }
    var LogOnScreenListener : View.OnClickListener = View.OnClickListener {
     val intent = Intent(this@LogOn , LogIn:: class.java)
        startActivity(intent)
    }
    private fun reload() {
        Firebaseref.auth = FirebaseAuth.getInstance()
    }
    fun writeNewUser( name: String, email: String,Img : String) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        IDs.UserId = userId
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE)
        IDs.sharedPreferences = sharedPref
        with(sharedPref.edit()){
            putString("UserId","${IDs.UserId}")
            apply()
        }

        val user = UserData(name, email ,Img,userId)

       val map = HashMap<String,String>()
        map.put("Name" , user.Name)
        map.put("Image" , user.Image)
        map.put("Email",user.Email)
        map.put("UserId",user.UserId)


        Firebaseref.database.child("/users/$userId").setValue(map).addOnSuccessListener() {
            Log.d("TAG","DB updated")
            val intent = Intent(this@LogOn , WelcomeScreen:: class.java)
            startActivity(intent)
        }.addOnFailureListener{
            Log.d("TAG","DB fail $it")
        }



    }


}
