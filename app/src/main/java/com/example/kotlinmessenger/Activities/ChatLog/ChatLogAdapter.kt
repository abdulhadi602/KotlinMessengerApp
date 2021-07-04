package com.example.kotlinmessenger.Activities.ChatLog


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessenger.Data.ChatMessage
import com.example.kotlinmessenger.Data.IDs
import com.example.kotlinmessenger.R

class ChatLogAdapter(ChatLog : ArrayList<ChatMessage>, con : Context, onClick : OnListItemClicked) : RecyclerView.Adapter<ChatLogAdapter.ViewHolder>() {
    var ChatLog : ArrayList<ChatMessage>
    var con : Context
    var onClick : OnListItemClicked
    val MESSAGE_FROM_USER = 0
    val MESSAGE_TO_USER = 1
    init {
        this.ChatLog = ChatLog
        this.con = con
        this.onClick = onClick
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View
        if (viewType == MESSAGE_FROM_USER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatdialogfrom,parent,false)
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatdialogto,parent,false)
        }

        return ViewHolder(
            view,
            onClick
        )
    }

    override fun getItemCount(): Int {
        return ChatLog.count()
    }

    override fun getItemViewType(position: Int): Int {
        if(ChatLog[position].userId.equals(IDs.UserId)){
            return MESSAGE_FROM_USER
        }else{
            return MESSAGE_TO_USER
        }

    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val temp = ChatLog[position]
        holder.ChatLogText.text = temp.text
    }


    class ViewHolder(itemView: View, OnClicked : OnListItemClicked) : RecyclerView.ViewHolder(itemView) , View.OnClickListener {
        //var Img : ImageView
        var  ChatLogText : TextView
        var OnUserClicked : OnListItemClicked
        init {
            this.OnUserClicked = OnClicked
           //this.Img = itemView.findViewById(R.id.ChatLogImage)

                this.ChatLogText = itemView.findViewById(R.id.ChatLogText)


            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            OnUserClicked.onItemClick(absoluteAdapterPosition)
        }

    }
    interface OnListItemClicked {
        fun onItemClick(position: Int)
    }
}
