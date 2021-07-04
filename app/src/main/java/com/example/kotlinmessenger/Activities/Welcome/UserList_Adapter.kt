package com.example.kotlinmessenger.Activities.Welcome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessenger.Data.UserData
import com.example.kotlinmessenger.R
import com.squareup.picasso.Picasso

class UserList_Adapter(Users : ArrayList<UserData>, con : Context, onClick : OnListItemClicked) : RecyclerView.Adapter<UserList_Adapter.ViewHolder>() {
    var Users : ArrayList<UserData>
    var con : Context
    var onClick : OnListItemClicked
    init {
     this.Users = Users
        this.con = con
        this.onClick = onClick
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userslistformat,parent,false)
        return ViewHolder(
            view,
            onClick
        )
    }

    override fun getItemCount(): Int {
        return Users.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val temp = Users[position]


            Picasso.with(con).load(temp.Image).into(holder.Img)


        holder.Name.text = temp.Name
        if(temp.UnreadMessages>0){
            holder.UnReadMessages.visibility = View.VISIBLE
            holder.UnReadMessagesNo.text = temp.UnreadMessages.toString()
        }else{
            holder.UnReadMessages.visibility = View.GONE
        }
        /**holder.OnUserClicked = object : OnListItemClicked {
            override fun onItemClick(position: Int) {

            }
        }**/
    }
    class ViewHolder(itemView: View , OnClicked : OnListItemClicked) : RecyclerView.ViewHolder(itemView) , View.OnClickListener {
        var Img : ImageView
        var  Name : TextView
        var UnReadMessages : ConstraintLayout
        var UnReadMessagesNo : TextView
        var OnUserClicked : OnListItemClicked
        init {
            this.OnUserClicked = OnClicked
            this.Img = itemView.findViewById(R.id.List_UserImg)
            this.Name = itemView.findViewById(R.id.List_Username)
            this.UnReadMessages = itemView.findViewById(R.id.UnreadMessages)
            this.UnReadMessagesNo = itemView.findViewById(R.id.UnreadMessagesNo)
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