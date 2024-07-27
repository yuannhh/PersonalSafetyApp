package com.mobdeve.s12.grp4.personalsafetyapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import android.app.AlertDialog
import android.content.Context
import androidx.navigation.fragment.findNavController

class SafetyZonesFragment : Fragment() {
    private lateinit var safetyZoneList: MutableList<SafetyZone>
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: SafetyZoneAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var imageButton2: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.safety_zones, container, false)

        // Initialize the safetyZoneList with a more detailed address format
        safetyZoneList = mutableListOf(
            SafetyZone("HOME", "Newton Street", "Makati", "Metro Manila", "Philippines"),
            SafetyZone("SCHOOL", "Escoda Street", "Manila", "Metro Manila", "Philippines"),
            SafetyZone("OFFICE", "Galileo Street", "Makati", "Metro Manila", "Philippines")
        )

        viewPager = rootView.findViewById(R.id.viewPagerSafetyZones)
        pagerAdapter = SafetyZoneAdapter(requireContext(), safetyZoneList)
        viewPager.adapter = pagerAdapter

        val addButton: Button = rootView.findViewById(R.id.addButton)
        addButton.setOnClickListener {
            showAddDialog()
        }

        val editButton: Button = rootView.findViewById(R.id.editButton)
        editButton.setOnClickListener {
            showEditDeleteDialog(viewPager.currentItem, true)
        }

        val deleteButton: Button = rootView.findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener {
            showEditDeleteDialog(viewPager.currentItem, false)
        }

        imageButton2.setOnClickListener {
            findNavController().navigate(R.id.action_viewSafetyZonesFragment_to_safetyStatusFragment)
        }

        // Initialize Location Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).apply {
            setMinUpdateIntervalMillis(5000)
            setMaxUpdateDelayMillis(10000)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    checkLocationInZones(location)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request necessary permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            startLocationUpdates()
        }

        return rootView
    }

    private fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (e: SecurityException) {
            // Handle the exception or notify the user
            Toast.makeText(requireContext(), "Location permissions are not granted.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (e: SecurityException) {
            // Handle the exception or notify the user
            Toast.makeText(requireContext(), "Location permissions are not granted.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationInZones(location: Location) {
        val currentLocation = LatLng(location.latitude, location.longitude)
        safetyZoneList.forEach { safetyZone ->
            val zoneLocation = getLatLngFromAddress(safetyZone.addressLine, safetyZone.city, safetyZone.stateProvince, safetyZone.country)
            val distance = calculateDistance(currentLocation, zoneLocation)
            if (distance < SAFE_ZONE_RADIUS) {
                // Notify user when entering the zone
                Toast.makeText(
                    requireContext(),
                    "Entering ${safetyZone.name} zone",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Optional: Notify user when leaving the zone
                Toast.makeText(
                    requireContext(),
                    "Leaving ${safetyZone.name} zone",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getLatLngFromAddress(addressLine: String, city: String, state: String, country: String): LatLng {
        val fullAddress = "$addressLine, $city, $state, $country"
        val geocoder = Geocoder(requireContext())
        val addressList = geocoder.getFromLocationName(fullAddress, 1)
        return if (addressList?.isNotEmpty() == true) {
            LatLng(addressList[0].latitude, addressList[0].longitude)
        } else {
            LatLng(0.0, 0.0) // Default to zero if address is not found
        }
    }

    private fun calculateDistance(loc1: LatLng, loc2: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(loc1.latitude, loc1.longitude, loc2.latitude, loc2.longitude, results)
        return results[0]
    }

    private fun showAddDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        builder.setTitle("Add Safety Zone")
        val dialogLayout = inflater.inflate(R.layout.dialog_add_edit_safety_zone, null)
        val nameEditText = dialogLayout.findViewById<EditText>(R.id.nameSafetyZone)
        val addressEditText = dialogLayout.findViewById<EditText>(R.id.addressLine)
        val cityEditText = dialogLayout.findViewById<EditText>(R.id.city)
        val stateEditText = dialogLayout.findViewById<EditText>(R.id.stateProvince)
        val countryEditText = dialogLayout.findViewById<EditText>(R.id.country)
        builder.setView(dialogLayout)

        builder.setPositiveButton("Add") { _, _ ->
            val name = nameEditText.text.toString()
            val addressLine = addressEditText.text.toString()
            val city = cityEditText.text.toString()
            val state = stateEditText.text.toString()
            val country = countryEditText.text.toString()
            if (name.isNotEmpty() && addressLine.isNotEmpty() && city.isNotEmpty() &&
                state.isNotEmpty() && country.isNotEmpty()) {
                val safetyZone = SafetyZone(name, addressLine, city, state, country)
                safetyZoneList.add(safetyZone)
                pagerAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "Please enter all details", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun showEditDeleteDialog(position: Int, isEdit: Boolean) {
        val action = if (isEdit) "Edit" else "Delete"
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("$action Safety Zone")
        builder.setPositiveButton(action) { _, _ ->
            if (isEdit) {
                showEditDialog(position)
            } else {
                safetyZoneList.removeAt(position)
                pagerAdapter.notifyDataSetChanged()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun showEditDialog(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        builder.setTitle("Edit Safety Zone")
        val dialogLayout = inflater.inflate(R.layout.dialog_add_edit_safety_zone, null)
        val nameEditText = dialogLayout.findViewById<EditText>(R.id.nameSafetyZone)
        val addressEditText = dialogLayout.findViewById<EditText>(R.id.addressLine)
        val cityEditText = dialogLayout.findViewById<EditText>(R.id.city)
        val stateEditText = dialogLayout.findViewById<EditText>(R.id.stateProvince)
        val countryEditText = dialogLayout.findViewById<EditText>(R.id.country)

        val safetyZone = safetyZoneList[position]
        nameEditText.setText(safetyZone.name)
        addressEditText.setText(safetyZone.addressLine)
        cityEditText.setText(safetyZone.city)
        stateEditText.setText(safetyZone.stateProvince)
        countryEditText.setText(safetyZone.country)

        builder.setView(dialogLayout)
        builder.setPositiveButton("Save") { _, _ ->
            val name = nameEditText.text.toString()
            val addressLine = addressEditText.text.toString()
            val city = cityEditText.text.toString()
            val state = stateEditText.text.toString()
            val country = countryEditText.text.toString()
            if (name.isNotEmpty() && addressLine.isNotEmpty() && city.isNotEmpty() &&
                state.isNotEmpty() && country.isNotEmpty()) {
                safetyZone.name = name
                safetyZone.addressLine = addressLine
                safetyZone.city = city
                safetyZone.stateProvince = state
                safetyZone.country = country
                pagerAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "Please enter all details", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val SAFE_ZONE_RADIUS = 100 // Example radius in meters
    }
}
