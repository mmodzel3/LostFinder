package com.github.mmodzel3.lostfinder.alert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.mmodzel3.lostfinder.R


class AlertAdapter : RecyclerView.Adapter<AlertViewHolder>() {
    companion object {
        const val ALERT_TYPE = 1
    }

    private var endAlertListener: EndAlertListener? = null

    var alerts: MutableList<Alert> = ArrayList()
        set(value: MutableList<Alert>) {
            value.filter { it.endDate == null }
            value.sortBy { it.sendDate }
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_alert_info, parent, false)

        return AlertViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert: Alert = alerts[position]

        holder.userName = alert.user.username
        holder.time = alert.sendDate
        holder.location = alert.location
        holder.title = alert.title
        holder.description = alert.description
        holder.range = alert.range

        holder.setOnEndAlertClickListener {
            onEndAlertClick(alert.id)
        }
    }

    override fun getItemCount(): Int {
        return alerts.size
    }

    override fun getItemViewType(position: Int): Int {
        return ALERT_TYPE
    }

    fun setOnEndAlertListener(listener: EndAlertListener) {
        endAlertListener = listener
    }

    private fun onEndAlertClick(alertId: String) {
        endAlertListener?.onEndAlert(alertId)
    }
}