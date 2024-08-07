package com.mobdeve.s12.grp4.personalsafetyapp

import android.Manifest
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
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.text.SimpleDateFormat
import java.util.*

class CheckInFragment : Fragment() {

    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private lateinit var locationOverlay: MyLocationNewOverlay
    private var currentLocation: GeoPoint? = null
    private lateinit var timeHandler: Handler
    private lateinit var timeRunnable: Runnable

    // Default location in Metro Manila
    private val defaultLocation = GeoPoint(14.599512, 120.984222) // Latitude and Longitude of Metro Manila

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_check_in, container, false)
        Configuration.getInstance().userAgentValue = requireActivity().packageName

        timeHandler = Handler(Looper.getMainLooper())

        val checkInButton: Button = rootView.findViewById(R.id.checkInButton)
        val confirmedButton: Button = rootView.findViewById(R.id.confirmedButton)
        val geolocationTextView: TextView = rootView.findViewById(R.id.geolocation)
        val existingTimeTextView: TextView = rootView.findViewById(R.id.existingTime)
        val mapOverlay: View = rootView.findViewById(R.id.mapOverlay)
        val imageButton2: ImageButton = rootView.findViewById(R.id.imageButton2)

        confirmedButton.visibility = View.GONE

        timeRunnable = object : Runnable {
            override fun run() {
                val currentTime = SimpleDateFormat("hh:mm a\nEEEE, MMMM d, yyyy (z)", Locale.getDefault()).format(Date())
                existingTimeTextView.text = "Current Time: $currentTime"
                timeHandler.postDelayed(this, 1000)
            }
        }

        timeHandler.post(timeRunnable)

        checkInButton.setOnClickListener {
            getLastLocation { location ->
                if (location != null) {
                    checkInButton.visibility = View.GONE
                    confirmedButton.visibility = View.VISIBLE

                    currentLocation = GeoPoint(location.latitude, location.longitude)

                    // Store data in SharedPreferences
                    val sharedPreferences = requireActivity().getSharedPreferences("safety_prefs", 0)
                    val editor = sharedPreferences.edit()
                    editor.putString("safety_status", "SAFE")
                    editor.putFloat("latitude", currentLocation!!.latitude.toFloat())
                    editor.putFloat("longitude", currentLocation!!.longitude.toFloat())
                    editor.apply()

                    geolocationTextView.text = "Geolocation: CONFIRMED"
                    geolocationTextView.setBackgroundColor(resources.getColor(R.color.green, null))

                    map.controller.setCenter(currentLocation)
                    map.controller.animateTo(currentLocation)

                    drawArrowToLocation(currentLocation!!)
                }
            }
        }

        imageButton2.setOnClickListener {
            findNavController().navigate(R.id.action_checkInFragment_to_safetyStatusFragment)
        }

        // Map overlay click listener
        mapOverlay.setOnClickListener {
            currentLocation?.let { loc ->
                val bundle = Bundle().apply {
                    putDouble("latitude", loc.latitude)
                    putDouble("longitude", loc.longitude)
                }
                findNavController().navigate(R.id.action_checkInFragment_to_fullMapFragment, bundle)
            } ?: run {
                // Default to the initial location (Metro Manila) if currentLocation is not available
                val bundle = Bundle().apply {
                    putDouble("latitude", defaultLocation.latitude)
                    putDouble("longitude", defaultLocation.longitude)
                }
                findNavController().navigate(R.id.action_checkInFragment_to_fullMapFragment, bundle)
            }
        }

        // Initialize map
        map = rootView.findViewById(R.id.mapView)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        mapController = map.controller
        mapController.setZoom(15.0)

        val locationProvider = GpsMyLocationProvider(requireContext())
        locationOverlay = MyLocationNewOverlay(locationProvider, map)
        locationOverlay.enableMyLocation()
        locationOverlay.isDrawAccuracyEnabled = true

        locationOverlay.runOnFirstFix {
            requireActivity().runOnUiThread {
                // Use default location initially
                val initialLocation = locationOverlay.myLocation ?: defaultLocation
                mapController.setCenter(initialLocation)
                mapController.animateTo(initialLocation)
                updateLocationInfo(initialLocation)
            }
        }

        map.overlays.add(locationOverlay)

        return rootView
    }

    private fun updateLocationInfo(geoPoint: GeoPoint?) {
        geoPoint?.let {
            // Update location info text view here if needed (optional)
        }
    }

    private fun drawArrowToLocation(location: GeoPoint) {
        val arrowOptions = Polyline().apply {
            addPoint(currentLocation)
            addPoint(location)
            color = resources.getColor(android.R.color.holo_red_dark, null)
            width = 5f
        }
        map.overlays.add(arrowOptions)
    }

    private fun getLastLocation(callback: (android.location.Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1001)
            return
        }
        locationOverlay.myLocationProvider.lastKnownLocation?.let {
            callback(it)
        }
    }
}
