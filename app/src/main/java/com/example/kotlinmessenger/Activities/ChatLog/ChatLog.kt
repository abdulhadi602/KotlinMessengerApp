package com.example.kotlinmessenger.Activities.ChatLog

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinmessenger.Model.ChatMessage
import com.example.kotlinmessenger.Model.IDs
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.Utils.Firebaseref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_chat_log.*
import java.util.*
import kotlin.collections.ArrayList

class ChatLog : AppCompatActivity(),
    ChatLogAdapter.OnListItemClicked {


lateinit var chatLog : ArrayList<ChatMessage>
    lateinit var chatLogAdapter: ChatLogAdapter
    var activityPaused : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        supportActionBar?.title = "ChatLog ${FirebaseAuth.getInstance().currentUser!!.displayName}"
        chatLog = ArrayList()
        val layout = LinearLayoutManager(this)
        layout.stackFromEnd = true
        ChatLogView.layoutManager = layout
        ChatLogView.setHasFixedSize(true)
        chatLogAdapter =
            ChatLogAdapter(
                chatLog,
                this,
                this
            )
        ChatLogView.adapter = chatLogAdapter
        FetchChanges()


        SendBtn.setOnClickListener(SendListener)
    }


    override fun onPause() {
        super.onPause()
        activityPaused = true
    }

    override fun onResume() {
        super.onResume()
        activityPaused = false
    }
    fun FetchChanges(){
        Firebaseref.database.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                // val value = dataSnapshot.get
                // userlistAdapter.clear();
                if(activityPaused) {
                 return
                }
                var Log =
                    dataSnapshot.child("/user-messages/${IDs.UserId}/${IDs.LastToId}").children
                chatLog.clear()

                for (chatMessage in Log) {
                    val map = chatMessage.value as HashMap<String, String>
                    val gson = Gson()
                    val jsonElement: JsonElement = gson.toJsonTree(map);
                    val data = gson.fromJson(jsonElement, ChatMessage::class.java)
                    chatLog.add(data)
                }
                chatLogAdapter.notifyDataSetChanged()
                if (chatLog.size > 0) {
                    ChatLogView.smoothScrollToPosition(chatLog.size - 1)
                }

                Log = dataSnapshot.child("/user-messages/${IDs.UserId}/${IDs.LastToId}").children
                for (chatMessage in Log) {
                    val map = chatMessage.value as HashMap<String, Any>
                    if (map.get("messageRead") == false) {
                        map.set("messageRead", true)
                        Firebaseref.database.child("/user-messages/${IDs.UserId}/${IDs.LastToId}/${chatMessage.key}")
                            .updateChildren(
                                map
                            ).addOnSuccessListener {

                        }
                    }

               }
        }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }


        })

    }
    fun fetchChatLog(){
        Firebaseref.database.child("/user-messages/${IDs.UserId}/${IDs.LastToId}").get().addOnSuccessListener {
            val Log = it.children
            chatLog.clear()
            for (chatMessage in Log){
                val map : HashMap<String, String> = chatMessage.value as HashMap<String, String>
                val gson = Gson()
                val jsonElement : JsonElement = gson.toJsonTree(map);
                val data = gson.fromJson(jsonElement, ChatMessage::class.java)
                chatLog.add(data)
            }
            chatLogAdapter.notifyDataSetChanged()

            ChatLogView.smoothScrollToPosition(Log.count()-1);
        }
    }
    var SendListener = View.OnClickListener {

        //CloseKeyInput()
        val message = ChatMessage(IDs.UserId,Date().time,ChatText.text.toString(),null,null)
        Firebaseref.database.child("/user-messages/${IDs.UserId}/${IDs.LastToId}").push().setValue(message).addOnSuccessListener() {
            Log.d("Chat","Messaged Saved in personal set")
            message.messageRead = false
            message.notificationReceived = false
            Firebaseref.database.child("/user-messages/${IDs.LastToId}/${IDs.UserId}").push().setValue(message)
        }.addOnFailureListener{
            Log.d("TAG","DB fail $it")
        }

        ChatText.setText("")
    }
    fun CloseKeyInput(){
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    override fun onItemClick(position: Int) {
        print("Message clicked")
    }
}
