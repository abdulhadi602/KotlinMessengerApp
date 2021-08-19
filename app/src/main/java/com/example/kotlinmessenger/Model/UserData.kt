package com.example.kotlinmessenger.Model

data class UserData(var Name : String, var Email : String, var Image : String,var UserId : String, var UnreadMessages : Int){
    constructor( Name : String , Email : String,Image : String, UserId : String) : this(Name,Email,Image,UserId,0)
}
data class Users(var ID: String, var userdata: UserData?)
