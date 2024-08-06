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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.util.Locale

class FullMapFragment : Fragment() {

    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private lateinit var searchView: SearchView
    private lateinit var backButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_full_map, container, false)
        Configuration.getInstance().userAgentValue = requireActivity().packageName

        searchView = rootView.findViewById(R.id.searchView)
        backButton = rootView.findViewById(R.id.backButton)

        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_fullMapFragment_to_safetyStatusFragment)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchLocation(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Initialize map
        map = rootView.findViewById(R.id.mapView)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        mapController = map.controller
        mapController.setZoom(15.0)

        val latitude = arguments?.getDouble("latitude")
        val longitude = arguments?.getDouble("longitude")
        val location = if (latitude != null && longitude != null) {
            GeoPoint(latitude, longitude)
        } else {
            GeoPoint(14.5995, 120.9842) // Default location to Metro Manila
        }

        val marker = Marker(map)
        marker.position = location
        marker.title = "Location"
        map.overlays.add(marker)

        mapController.setCenter(location)

        return rootView
    }

    private fun searchLocation(query: String) {
        lifecycleScope.launch {
            val location = getLocationFromQuery(query) ?: GeoPoint(14.5995, 120.9842) // Default location if no result

            map.overlays.clear()
            val marker = Marker(map)
            marker.position = location
            marker.title = "Searched Location"
            map.overlays.add(marker)

            mapController.setCenter(location)
            mapController.animateTo(location)
        }
    }

    private suspend fun getLocationFromQuery(query: String): GeoPoint? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return withContext(Dispatchers.IO) {
            try {
                // Use the deprecated method but ensure proper handling
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    GeoPoint(address.latitude, address.longitude)
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
