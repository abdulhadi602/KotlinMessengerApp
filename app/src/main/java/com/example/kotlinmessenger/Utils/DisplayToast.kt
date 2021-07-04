package com.example.kotlinmessenger.Utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast

class DisplayToast(Message : String , Long : Boolean , position : Position ,con : Context) {
    init {
        var toast : Toast
        if (Long) {
            toast = Toast.makeText(con, Message, Toast.LENGTH_LONG)

        }else{
            toast = Toast.makeText(con, Message, Toast.LENGTH_SHORT)
        }
        when(position){
            Position.TOP -> toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
            Position.BOTTOM -> toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
            Position.CENTER -> toast.setGravity(Gravity.CENTER, 0, 0)
        }
        toast.show()
    }
}
enum class Position{
    TOP,
    BOTTOM,
    CENTER
}