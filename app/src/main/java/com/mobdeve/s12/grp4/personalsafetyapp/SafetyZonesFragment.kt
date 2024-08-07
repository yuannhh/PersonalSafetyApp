package com.mobdeve.s12.grp4.personalsafetyapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class SafetyZonesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var safetyZoneAdapter: SafetyZoneAdapter
    private lateinit var addButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.safety_zones, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewSafetyZones)
        addButton = view.findViewById(R.id.addButton)
        editButton = view.findViewById(R.id.editButton)
        deleteButton = view.findViewById(R.id.deleteButton)

        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return view
        }

        fetchSafetyZones(userId)

        addButton.setOnClickListener {
            replaceFragment(AddSafetyZoneActivity())
        }

        editButton.setOnClickListener {
            replaceFragment(EditSafetyZoneActivity())
        }

        deleteButton.setOnClickListener {
            replaceFragment(DeleteSafetyZoneActivity())
        }

        return view
    }

    private fun fetchSafetyZones(userId: Int) {
        val url = "http://192.168.254.128/mobdeve/safety_zones.php"

        val formBody = FormBody.Builder()
            .add("user_id", userId.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SafetyZonesFragment", "Error: ${e.message}")
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Failed to fetch safety zones", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("SafetyZonesFragment", "Response Data: $responseData") // Log the raw response data

                requireActivity().runOnUiThread {
                    if (response.isSuccessful && responseData != null) {
                        try {
                            val jsonArray = JSONArray(responseData)
                            val safetyZones = mutableListOf<SafetyZone>()
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val safetyZone = SafetyZone(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("user_id"),
                                    jsonObject.getString("name"),
                                    jsonObject.getString("addressLine"),
                                    jsonObject.getString("city"),
                                    jsonObject.getString("state"),
                                    jsonObject.getString("country")
                                )
                                safetyZones.add(safetyZone)
                            }
                            safetyZoneAdapter = SafetyZoneAdapter(safetyZones)
                            recyclerView.adapter = safetyZoneAdapter
                        } catch (e: Exception) {
                            Log.e("SafetyZonesFragment", "Parsing Error: ${e.message}")
                            Toast.makeText(context, "Failed to parse data", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(null) // Optional: Add to back stack if you want the user to be able to navigate back
            .commit()
    }
}
