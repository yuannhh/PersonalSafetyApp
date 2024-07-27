package com.mobdeve.s12.grp4.personalsafetyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SafetyZoneAdapter(
    private val context: Context,
    private val safetyZones: List<SafetyZone>
) : RecyclerView.Adapter<SafetyZoneAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.zoneName)
        val addressTextView: TextView = itemView.findViewById(R.id.zoneAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.safety_zone_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val safetyZone = safetyZones[position]
        holder.nameTextView.text = safetyZone.name
        holder.addressTextView.text = "${safetyZone.addressLine}, ${safetyZone.city}, ${safetyZone.stateProvince}, ${safetyZone.country}"
    }

    override fun getItemCount(): Int = safetyZones.size
}
