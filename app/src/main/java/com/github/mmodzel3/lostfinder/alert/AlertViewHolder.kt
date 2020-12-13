package com.github.mmodzel3.lostfinder.alert

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.location.Location
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val userNameTextView: TextView = itemView.findViewById(R.id.activity_alert_info_tv_user_name)
    private val titleTextView: TextView = itemView.findViewById(R.id.activity_alert_info_tv_title)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.activity_alert_info_tv_description)
    private val timeTextView: TextView = itemView.findViewById(R.id.activity_alert_info_tv_time)
    private val locationTextView: TextView = itemView.findViewById(R.id.activity_alert_info_tv_location)
    private val rangeTextView: TextView = itemView.findViewById(R.id.activity_alert_info_tv_range)
    private val distanceString: String = itemView.context.getString(R.string.activity_alert_info_distance_text)
    private val endAlertButton: ImageButton = itemView.findViewById(R.id.activity_alert_info_bt_end_alert)

    var userName: CharSequence
        get() {
            return userNameTextView.text
        }

        set(value: CharSequence) {
            userNameTextView.text = value
        }

    var title: CharSequence
        get() {
            return titleTextView.text
        }

        set(value: CharSequence) {
            titleTextView.text = value
        }

    var description: CharSequence
        get() {
            return descriptionTextView.text
        }

        set(value: CharSequence) {
            descriptionTextView.text = value
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

    var range: Double
        get() {
            val rangeSplit = rangeTextView.text.split("")
            return rangeSplit[0].toDouble()
        }

        set(value: Double) {
            val roundedValue: Double = (round(value * 100) / 100)
            val range: String = "$roundedValue $distanceString"
            rangeTextView.text = range
        }

    var location: Location?
        get() {
            val locationString: String = locationTextView.text.toString()

            if (locationString != "") {
                val locationSplit: List<String> = locationString.split(", ")

                val longitude = locationSplit[0].removePrefix("(").toDouble()
                val latitude = locationSplit[1].removeSuffix(")").toDouble()

                return Location(longitude, latitude)
            } else {
                return null
            }
        }

        set(value: Location?) {
            val locationString: String = if (location != null)
                "(${value!!.longitude}, ${value.latitude})" else ""

            locationTextView.text = locationString
        }

    fun setOnEndAlertClickListener(listener: () -> Unit) {
        endAlertButton.setOnClickListener {
            it.isEnabled = false
            listener()
            it.isEnabled = true
        }
    }

    private fun sameDay(date1: Date, date2: Date): Boolean {
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return fmt.format(date1) == fmt.format(date2)
    }
}