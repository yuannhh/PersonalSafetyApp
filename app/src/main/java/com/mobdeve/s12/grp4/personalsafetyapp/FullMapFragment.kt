package com.mobdeve.s12.grp4.personalsafetyapp

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class FullMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var searchView: SearchView
    private lateinit var backButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_full_map, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.fullMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchView = rootView.findViewById(R.id.searchView)
        backButton = rootView.findViewById(R.id.backButton)

        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_fullMapFragment_to_safetyStatusFragment)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    // Handle search query and move to the searched location
                    searchLocation(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optionally handle text changes
                return false
            }
        })

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Get the passed location from arguments
        val latitude = arguments?.getDouble("latitude")
        val longitude = arguments?.getDouble("longitude")
        val location = if (latitude != null && longitude != null) {
            LatLng(latitude, longitude)
        } else {
            // Default location if none is provided
            LatLng(14.5995, 120.9842) // Metro Manila
        }

        map.addMarker(MarkerOptions().position(location).title("Location"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    private fun searchLocation(query: String) {
        lifecycleScope.launch {
            val location = getLocationFromQuery(query) ?: LatLng(14.5995, 120.9842) // Default location if no result

            map.clear()
            map.addMarker(MarkerOptions().position(location).title("Searched Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

    private suspend fun getLocationFromQuery(query: String): LatLng? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return withContext(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocationName(query, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    LatLng(address.latitude, address.longitude)
                } else {
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
}
