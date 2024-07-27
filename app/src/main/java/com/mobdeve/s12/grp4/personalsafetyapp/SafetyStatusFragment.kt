package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SafetyStatusFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_safety_status, container, false)

        // Set up buttons and their click listeners
        val checkInButton: Button = view.findViewById(R.id.checkInButton)
        checkInButton.setOnClickListener {
            findNavController().navigate(R.id.action_safetyStatusFragment_to_checkInFragment)
        }

        val viewSafetyZonesButton: Button = view.findViewById(R.id.viewSafetyZonesButton)
        viewSafetyZonesButton.setOnClickListener {
            findNavController().navigate(R.id.action_safetyStatusFragment_to_viewSafetyZonesFragment)
        }

        val viewIncidentButton: Button = view.findViewById(R.id.viewIncidentButton)
        viewIncidentButton.setOnClickListener {
            findNavController().navigate(R.id.action_safetyStatusFragment_to_viewIncidentFragment)
        }

        val recordIncidentButton: Button = view.findViewById(R.id.recordIncidentButton)
        recordIncidentButton.setOnClickListener {
            findNavController().navigate(R.id.action_safetyStatusFragment_to_recordIncidentFragment)
        }

        val autoNotificationsButton: Button = view.findViewById(R.id.autoNotificationsButton)
        autoNotificationsButton.setOnClickListener {
            findNavController().navigate(R.id.action_safetyStatusFragment_to_autoNotificationsFragment)
        }

        return view
    }
}
