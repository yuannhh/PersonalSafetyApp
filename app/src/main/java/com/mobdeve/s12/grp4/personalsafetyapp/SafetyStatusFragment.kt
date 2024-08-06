package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class SafetyStatusFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_safety_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("safety_prefs", 0)
        val safetyStatus = sharedPreferences.getString("safety_status", "UNKNOWN")
        val latitude = sharedPreferences.getFloat("latitude", 0.0f)
        val longitude = sharedPreferences.getFloat("longitude", 0.0f)

        val statusTextView: TextView = view.findViewById(R.id.currentSafetyStatusTextView)
        val mapPlaceholder: View = view.findViewById(R.id.currentLocationMapPlaceholder)

        statusTextView.text = "Current Safety Status: $safetyStatus"

        if (latitude != 0.0f && longitude != 0.0f) {
            // Display the map with the fetched location
            val mapView = mapPlaceholder as MapView
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)

            val mapController = mapView.controller
            mapController.setZoom(15.0)
            val location = GeoPoint(latitude.toDouble(), longitude.toDouble())
            mapController.setCenter(location)
            mapController.animateTo(location)
        }

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
    }
}
