package com.example.kotlinmessenger.Activities.Welcome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinmessenger.Activities.ChatLog.ChatLog
import com.example.kotlinmessenger.Model.IDs
import com.example.kotlinmessenger.Model.UserData
import com.example.kotlinmessenger.Model.Users
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.Utils.Firebaseref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_welcome_screen.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class WelcomeScreen : AppCompatActivity(),
    UserList_Adapter.OnListItemClicked {
    lateinit var UserList: ArrayList<UserData>
    lateinit var userlistAdapter: UserList_Adapter
    lateinit var Users: ArrayList<Users>

    companion object {
        var ActivityOpened: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)
        UserList = ArrayList()
        Users = ArrayList()
        val linear = LinearLayoutManager(this)
        UsersList.setLayoutManager(linear)
        UsersList.setHasFixedSize(true)

        userlistAdapter =
            UserList_Adapter(
                UserList,
                this,
                this
            )
        UsersList.adapter = userlistAdapter


        var ref = FirebaseStorage.getInstance()
            .getReference(FirebaseAuth.getInstance().currentUser!!.photoUrl.toString())
        ref.downloadUrl.addOnSuccessListener {
            Picasso.with(applicationContext).load(it).into(ProfilePic)
        }

        //DownloadImageTask(ProfilePic).execute(FirebaseAuth.getInstance().currentUser!!.photoUrl.toString());
        Log.d("URL", FirebaseAuth.getInstance().currentUser!!.photoUrl.toString())
        User.text = "Welcome " + FirebaseAuth.getInstance().currentUser!!.displayName


        Firebaseref.init()
        Firebaseref.database.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                // val value = dataSnapshot.get
                // userlistAdapter.clear();
                UpdateData(dataSnapshot)


            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }


        })


    }


    override fun onResume() {
        super.onResume()
        ActivityOpened++
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(applicationContext, ChatLog::class.java)
        IDs.LastToId = Users.get(position).ID
        startActivity(intent)
    }

    private fun UpdateData(dataSnapshot: DataSnapshot) {
        UserList.clear()
        Users.clear()

        val users = dataSnapshot.child("users").children
        for (d in users) {
            var user = Users(d.key.toString(), null)

            val map: HashMap<String, String> = d.value as HashMap<String, String>
            val gson = Gson()
            val jsonElement: JsonElement = gson.toJsonTree(map)
            user.userdata = gson.fromJson(jsonElement, UserData::class.java)
            val temp = user.userdata
            var PendingNotificationsPerUser = 0
            if (temp != null && !temp.UserId.equals(IDs.UserId)) {
                val Log = dataSnapshot.child("/user-messages/${IDs.UserId}/${user.ID}").children
                var chatMessagesNotifcationUpdate : HashMap<String,Any> = HashMap()
                for (chatMessage in Log) {
                    val map = chatMessage.value as HashMap<String, Any>
                    if (map.get("messageRead") == false) {
                        temp.UnreadMessages += 1
                        /**if (map.get("notificationReceived") == false) {
                            PendingNotificationsPerUser += 1
                            map.set("notificationReceived", true)
                            chatMessagesNotifcationUpdate.put(chatMessage.key.toString(),map)
                        }**/
                    }
                }
                /**if(PendingNotificationsPerUser>0){
                    Notification(
                        this@WelcomeScreen,
                        "Messages",
                        "${PendingNotificationsPerUser} new message(s) from ${temp.Name}"
                    )
                    Firebaseref.database.child("/user-messages/${IDs.UserId}/${user.ID}")
                        .updateChildren(
                            chatMessagesNotifcationUpdate
                        ).addOnSuccessListener {

                        }
                }**/
                UserList.add(temp)
                Users.add(user)
            }
        }
        userlistAdapter.notifyDataSetChanged()
    }
}
