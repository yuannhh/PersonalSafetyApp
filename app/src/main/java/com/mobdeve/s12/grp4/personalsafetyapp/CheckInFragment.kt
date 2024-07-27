package com.mobdeve.s12.grp4.personalsafetyapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.text.SimpleDateFormat
import java.util.*

class CheckInFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null
    private lateinit var timeHandler: Handler
    private lateinit var timeRunnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_check_in, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        timeHandler = Handler(Looper.getMainLooper())

        val checkInButton: Button = rootView.findViewById(R.id.checkInButton)
        val confirmedButton: Button = rootView.findViewById(R.id.confirmedButton)
        val geolocationTextView: TextView = rootView.findViewById(R.id.geolocation)
        val existingTimeTextView: TextView = rootView.findViewById(R.id.existingTime)
        val mapOverlay: View = rootView.findViewById(R.id.mapOverlay)
        val imageButton2: ImageButton = rootView.findViewById(R.id.imageButton2)

        timeRunnable = object : Runnable {
            override fun run() {
                val currentTime = SimpleDateFormat("hh:mm a\nEEEE, MMMM d, yyyy (z)", Locale.getDefault()).format(Date())
                existingTimeTextView.text = "Current Time: $currentTime"
                timeHandler.postDelayed(this, 1000) // Update every second
            }
        }

        timeHandler.post(timeRunnable)

        checkInButton.setOnClickListener {
            getLastLocation { location ->
                if (location != null) {
                    checkInButton.visibility = View.GONE
                    confirmedButton.visibility = View.VISIBLE

                    currentLocation = LatLng(location.latitude, location.longitude)

                    geolocationTextView.text = "Geolocation: CONFIRMED"
                    geolocationTextView.setBackgroundColor(resources.getColor(R.color.green, null))

                    map.addMarker(MarkerOptions().position(currentLocation!!).title("Confirmed Location"))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation!!, 15f))

                    // Draw arrow to the confirmed location
                    drawArrowToLocation(currentLocation!!)
                }
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapOverlay.setOnClickListener {
            currentLocation?.let { loc ->
                val bundle = Bundle().apply {
                    putDouble("latitude", loc.latitude)
                    putDouble("longitude", loc.longitude)
                }
                findNavController().navigate(R.id.action_CheckInFragment_to_FullMapFragment, bundle)
            }
        }

        imageButton2.setOnClickListener {
            findNavController().navigate(R.id.action_CheckInFragment_to_safetyStatusFragment)
        }

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Center map on the current location if available
        currentLocation?.let {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
        } ?: run {
            // Set default location to Metro Manila, Philippines
            val defaultLocation = LatLng(14.5995, 120.9842) // Metro Manila
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(callback: (android.location.Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1001)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            callback(location)
        }
    }

    private fun drawArrowToLocation(location: LatLng) {
        // Example arrow drawing implementation; you may need to adjust it based on your requirements
        val arrowOptions = PolylineOptions().apply {
            add(currentLocation!!)
            add(location)
            color(resources.getColor(android.R.color.holo_red_dark, null)) // Set color to red
            width(5f) // Adjust width as needed
        }
        map.addPolyline(arrowOptions)
    }
}
