package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.text.SimpleDateFormat
import java.util.Locale

class RecordIncidentFragment : Fragment() {

    private lateinit var incidentTypeEditText: EditText
    private lateinit var incidentDetailsEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var statusSpinner: Spinner
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button
    private lateinit var requestQueue: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.record_incident, container, false)

        incidentTypeEditText = view.findViewById(R.id.incidentTypeEditText)
        incidentDetailsEditText = view.findViewById(R.id.incidentDetailsEditText)
        locationEditText = view.findViewById(R.id.locationEditText)
        statusSpinner = view.findViewById(R.id.statusSpinner)
        submitButton = view.findViewById(R.id.submitButton)
        cancelButton = view.findViewById(R.id.cancelButton)

        requestQueue = Volley.newRequestQueue(requireContext())

        // Set up the Spinner with status options
        val statuses = arrayOf("Reported", "In Progress", "Resolved")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = adapter

        submitButton.setOnClickListener {
            submitIncident()
        }

        cancelButton.setOnClickListener {
            // Handle cancel button click (e.g., clear fields or navigate away)
            Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun submitIncident() {
        val incidentType = incidentTypeEditText.text.toString().trim()
        val incidentDetails = incidentDetailsEditText.text.toString().trim()
        val location = locationEditText.text.toString().trim()
        val status = statusSpinner.selectedItem.toString()
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
        val userId = 1 // Replace with actual user ID or fetch from logged-in user data

        if (incidentType.isEmpty() || incidentDetails.isEmpty() || location.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        // URL of the PHP script that handles the database insertion
        val url = "http://192.168.254.128/mobdeve/submit_incident.php"

        // Create a request to send data to PHP script
        val requestBody = "user_id=$userId&incident_type=$incidentType&incident_details=$incidentDetails&location=$location&status=$status&timestamp=$timestamp"
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(requireContext(), "Incident recorded successfully", Toast.LENGTH_LONG).show()
                clearFields()
            },
            Response.ErrorListener { error ->
                Log.e("SubmitIncident", "Error: ${error.message}")
                Toast.makeText(requireContext(), "Failed to record incident", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = mutableMapOf<String, String>()
                params["user_id"] = userId.toString()
                params["incident_type"] = incidentType
                params["incident_details"] = incidentDetails
                params["location"] = location
                params["status"] = status
                params["timestamp"] = timestamp
                return params
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun clearFields() {
        incidentTypeEditText.text.clear()
        incidentDetailsEditText.text.clear()
        locationEditText.text.clear()
        statusSpinner.setSelection(0)
    }
}
