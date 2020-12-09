package com.github.mmodzel3.lostfinder.chat

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.mmodzel3.lostfinder.R
import java.text.SimpleDateFormat
import java.util.*

open class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    protected val userNameTextView: TextView = itemView.findViewById(R.id.activity_chat_msg_tv_user_name)
    protected val textTextView: TextView = itemView.findViewById(R.id.activity_chat_msg_tv_text)
    protected val timeTextView: TextView = itemView.findViewById(R.id.activity_chat_msg_tv_time)

    var text: CharSequence
        get() {
            return textTextView.text
        }

        set(value: CharSequence) {
            textTextView.text = value
        }

    var time: Date
        get() {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return dateFormat.parse(timeTextView.text.toString())!!
        }

        set(value: Date) {
            if (sameDay(value, Date())) {
                val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                timeTextView.text = dateFormat.format(value)
            } else {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                timeTextView.text = dateFormat.format(value)
            }
        }

    private fun sameDay(date1: Date, date2: Date): Boolean {
        val fmt = SimpleDateFormat("yyyyMMdd")
        return fmt.format(date1) == fmt.format(date2)
    }
}