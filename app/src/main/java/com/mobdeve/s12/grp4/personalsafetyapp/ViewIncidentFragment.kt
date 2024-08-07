package com.mobdeve.s12.grp4.personalsafetyapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s12.grp4.personalsafetyapp.databinding.ViewIncidentBinding
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class ViewIncidentFragment : Fragment() {

    private var _binding: ViewIncidentBinding? = null
    private val binding get() = _binding!!

    private var incidents = mutableListOf<Incident>()
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ViewIncidentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get user ID from SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)
        if (userId == -1) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Setup RecyclerView
        binding.recyclerViewIncident.layoutManager = LinearLayoutManager(context)
        val adapter = IncidentAdapter()
        binding.recyclerViewIncident.adapter = adapter

        // Fetch incidents for the logged-in user
        fetchIncidentDetails(userId)

        binding.viewAlertButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, ViewAlertFragment()) .commit()
        }

        binding.clearHistoryButton.setOnClickListener {
            showClearHistoryConfirmationDialog()
        }

        binding.imageButton2.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, SafetyStatusFragment())
                .commit()
        }
    }

    private fun fetchIncidentDetails(userId: Int) {
        val url = "http://192.168.254.128/mobdeve/incidents.php"

        val formBody = FormBody.Builder()
            .add("user_id", userId.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Failed to fetch incident details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                requireActivity().runOnUiThread {
                    if (response.isSuccessful && responseData != null) {
                        try {
                            val jsonArray = JSONArray(responseData)
                            incidents.clear()
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val incident = Incident(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("user_id"),
                                    jsonObject.getString("type"),
                                    jsonObject.getString("details"),
                                    jsonObject.getString("location"),
                                    jsonObject.getString("timestamp"),
                                    jsonObject.getString("status")
                                )
                                incidents.add(incident)
                            }
                            (binding.recyclerViewIncident.adapter as IncidentAdapter).notifyDataSetChanged()
                            updateEmptyState()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to parse data", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun updateEmptyState() {
        val recyclerView = binding.recyclerViewIncident
        val noIncidentsTextView = binding.root.findViewById<TextView>(R.id.noIncidentsTextView)

        if (incidents.isEmpty()) {
            recyclerView.visibility = View.GONE
            noIncidentsTextView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            noIncidentsTextView.visibility = View.GONE
        }
    }

    private fun showClearHistoryConfirmationDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Clear Incident History")
            setMessage("Are you sure you want to clear all incidents?")
            setPositiveButton("Yes") { _, _ ->
                // Handle clear history logic here
                incidents.clear()
                (binding.recyclerViewIncident.adapter as IncidentAdapter).notifyDataSetChanged()
                updateEmptyState()
                Toast.makeText(requireContext(), "Incident history cleared", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("No", null)
            show()
        }
    }

    inner class IncidentAdapter : RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder>() {

        inner class IncidentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val typeTextView: TextView = itemView.findViewById(R.id.incidentTypeTextView)
            private val detailsTextView: TextView = itemView.findViewById(R.id.incidentDetailsTextView)
            private val locationTextView: TextView = itemView.findViewById(R.id.incidentLocationTextView)
            private val timestampTextView: TextView = itemView.findViewById(R.id.incidentTimestampTextView)
            private val statusTextView: TextView = itemView.findViewById(R.id.incidentStatusTextView)

            fun bind(incident: Incident) {
                typeTextView.text = incident.type
                detailsTextView.text = incident.details
                locationTextView.text = incident.location
                timestampTextView.text = incident.timestamp
                statusTextView.text = incident.status
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidentViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_incident, parent, false)
            return IncidentViewHolder(view)
        }

        override fun onBindViewHolder(holder: IncidentViewHolder, position: Int) {
            holder.bind(incidents[position])
        }

        override fun getItemCount(): Int = incidents.size
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
