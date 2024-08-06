package com.mobdeve.s12.grp4.personalsafetyapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SafetyZoneAdapter(private val safetyZones: List<SafetyZone>) : RecyclerView.Adapter<SafetyZoneAdapter.SafetyZoneViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SafetyZoneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_safety_zone, parent, false)
        return SafetyZoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: SafetyZoneViewHolder, position: Int) {
        val safetyZone = safetyZones[position]
        holder.bind(safetyZone)
    }

    override fun getItemCount() = safetyZones.size

    class SafetyZoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numberTextView: TextView = itemView.findViewById(R.id.safetyZoneNumber)
        private val nameTextView: TextView = itemView.findViewById(R.id.safetyZoneName)
        private val addressTextView: TextView = itemView.findViewById(R.id.safetyZoneAddress)

        fun bind(safetyZone: SafetyZone) {
            numberTextView.text = safetyZone.id.toString() // Use the ID from SQL
            nameTextView.text = safetyZone.name
            addressTextView.text = "${safetyZone.addressLine}, ${safetyZone.city}, ${safetyZone.state}, ${safetyZone.country}"
        }
    }
}
