package com.mobdeve.s12.grp4.personalsafetyapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AutoNotificationAdapter(private val notifications: List<AutoNotification>) : RecyclerView.Adapter<AutoNotificationAdapter.AutoNotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoNotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return AutoNotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: AutoNotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification)
    }

    override fun getItemCount() = notifications.size

    class AutoNotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.notificationTextView)
        private val intervalTextView: TextView = itemView.findViewById(R.id.intervalTextView)
        private val timeUnitTextView: TextView = itemView.findViewById(R.id.timeUnitTextView)

        fun bind(notification: AutoNotification) {
            textView.text = notification.notificationText
            intervalTextView.text = notification.interval.toString()
            timeUnitTextView.text = notification.timeUnit
        }
    }
}
