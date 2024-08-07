package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

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
        val mapPlaceholder: MapView = view.findViewById(R.id.currentLocationMapPlaceholder)

        statusTextView.text = "Current Safety Status: $safetyStatus"

        if (latitude != 0.0f && longitude != 0.0f) {
            // Display the map with the fetched location
            mapPlaceholder.setTileSource(TileSourceFactory.MAPNIK)
            mapPlaceholder.setMultiTouchControls(true)

            val mapController = mapPlaceholder.controller
            mapController.setZoom(15.0)
            val location = GeoPoint(latitude.toDouble(), longitude.toDouble())
            mapController.setCenter(location)
            mapController.animateTo(location)

            // Add a red marker to the map
            val marker = Marker(mapPlaceholder)
            marker.position = location
            marker.icon = resources.getDrawable(R.drawable.ic_red_marker) // Use a red marker drawable
            marker.title = "Confirmed Location"
            mapPlaceholder.overlays.add(marker)
        }

        // Set up buttons and their click listeners
        val checkInButton: Button = view.findViewById(R.id.checkInButton)
        checkInButton.setOnClickListener {
            replaceFragment(CheckInFragment())
        }

        val viewSafetyZonesButton: Button = view.findViewById(R.id.viewSafetyZonesButton)
        viewSafetyZonesButton.setOnClickListener {
            replaceFragment(SafetyZonesFragment())
        }

        val viewIncidentButton: Button = view.findViewById(R.id.viewIncidentButton)
        viewIncidentButton.setOnClickListener {
            replaceFragment(ViewIncidentFragment())
        }

        val recordIncidentButton: Button = view.findViewById(R.id.recordIncidentButton)
        recordIncidentButton.setOnClickListener {
            replaceFragment(RecordIncidentFragment())
        }

        val autoNotificationsButton: Button = view.findViewById(R.id.autoNotificationsButton)
        autoNotificationsButton.setOnClickListener {
            replaceFragment(AutoNotificationsFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}