package com.mobdeve.s12.grp4.personalsafetyapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class SafetyStatusFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_safety_status, container, false)

        // Set up buttons and their click listeners
        val checkInButton: Button = view.findViewById(R.id.checkInButton)
        checkInButton.setOnClickListener {
            val intent = Intent(activity, CheckInFragment::class.java)
            startActivity(intent)
        }

        val viewSafetyZonesButton: Button = view.findViewById(R.id.viewSafetyZonesButton)
        viewSafetyZonesButton.setOnClickListener {
            val intent = Intent(activity, SafetyZonesFragment::class.java)
            startActivity(intent)
        }

        val viewIncidentButton: Button = view.findViewById(R.id.viewIncidentButton)
        viewIncidentButton.setOnClickListener {
            val intent = Intent(activity, ViewIncidentFragment::class.java)
            startActivity(intent)
        }

        val recordIncidentButton: Button = view.findViewById(R.id.recordIncidentButton)
        recordIncidentButton.setOnClickListener {
            val intent = Intent(activity, RecordIncidentFragment::class.java)
            startActivity(intent)
        }

        val autoNotificationsButton: Button = view.findViewById(R.id.autoNotificationsButton)
        autoNotificationsButton.setOnClickListener {
            val intent = Intent(activity, AutoNotificationsFragment::class.java)
            startActivity(intent)
        }

        return view
    }
}