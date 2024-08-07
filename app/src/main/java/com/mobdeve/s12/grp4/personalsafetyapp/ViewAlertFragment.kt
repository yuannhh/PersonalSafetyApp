package com.mobdeve.s12.grp4.personalsafetyapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.grp4.personalsafetyapp.databinding.ViewAlertBinding
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import android.widget.TextView

class ViewAlertFragment : Fragment() {

    private var _binding: ViewAlertBinding? = null
    private val binding get() = _binding!!

    private var alerts = mutableListOf<Alert>()
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ViewAlertBinding.inflate(inflater, container, false)
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
        binding.recyclerViewAlert.layoutManager = LinearLayoutManager(context)
        val adapter = AlertAdapter()
        binding.recyclerViewAlert.adapter = adapter

        // Fetch alerts for the logged-in user
        fetchAlerts(userId)

        binding.viewIncidentButton.setOnClickListener {
            replaceFragment(ViewIncidentFragment())
        }

        binding.clearHistoryButton.setOnClickListener {
            showClearHistoryConfirmationDialog()
        }

        binding.imageButton2.setOnClickListener {
            replaceFragment(SafetyStatusFragment())
        }
    }

    private fun fetchAlerts(userId: Int) {
        val url = "http://192.168.254.128/mobdeve/alert.php"

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
                    Toast.makeText(context, "Failed to fetch alerts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                requireActivity().runOnUiThread {
                    if (response.isSuccessful && responseData != null) {
                        try {
                            val jsonArray = JSONArray(responseData)
                            alerts.clear()
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val alert = Alert(
                                    jsonObject.getInt("id"),
                                    jsonObject.getString("name"),
                                    jsonObject.getString("type"),
                                    jsonObject.getInt("user_id"),
                                    jsonObject.getString("timestamp")
                                )
                                alerts.add(alert)
                            }
                            (binding.recyclerViewAlert.adapter as AlertAdapter).notifyDataSetChanged()
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
        val recyclerView = binding.recyclerViewAlert
        val noAlertsTextView = binding.root.findViewById<TextView>(R.id.noAlertsTextView)

        if (alerts.isEmpty()) {
            recyclerView.visibility = View.GONE
            noAlertsTextView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            noAlertsTextView.visibility = View.GONE
        }
    }

    private fun showClearHistoryConfirmationDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Clear Alert History")
            setMessage("Are you sure you want to clear all alerts?")
            setPositiveButton("Yes") { _, _ ->
                // Handle clear history logic here
                alerts.clear()
                (binding.recyclerViewAlert.adapter as AlertAdapter).notifyDataSetChanged()
                updateEmptyState()
                Toast.makeText(requireContext(), "Alert history cleared", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("No", null)
            show()
        }
    }

    inner class AlertAdapter : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

        inner class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nameTextView: TextView = itemView.findViewById(R.id.alertNameTextView)
            private val typeTextView: TextView = itemView.findViewById(R.id.alertTypeTextView)
            private val timestampTextView: TextView = itemView.findViewById(R.id.alertTimestampTextView)

            fun bind(alert: Alert) {
                nameTextView.text = alert.name
                typeTextView.text = alert.type
                timestampTextView.text = alert.timestamp
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alert, parent, false)
            return AlertViewHolder(view)
        }

        override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
            holder.bind(alerts[position])
        }

        override fun getItemCount(): Int = alerts.size
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
