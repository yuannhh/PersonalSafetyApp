package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class AddNotificationFragment : Fragment() {

    private lateinit var notificationTextEditText: EditText
    private lateinit var intervalEditText: EditText
    private lateinit var timeUnitSpinner: Spinner
    private lateinit var addButton: Button
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_add_notifis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationTextEditText = view.findViewById(R.id.notificationTextEditText)
        intervalEditText = view.findViewById(R.id.intervalEditText)
        timeUnitSpinner = view.findViewById(R.id.timeUnitSpinner)
        addButton = view.findViewById(R.id.addButton)

        addButton.setOnClickListener {
            val notificationText = notificationTextEditText.text.toString()
            val interval = intervalEditText.text.toString().toLongOrNull()
            val timeUnit = timeUnitSpinner.selectedItem?.toString() ?: ""

            if (interval != null && timeUnit.isNotEmpty()) {
                fetchUserIdAndAddNotification(notificationText, interval, timeUnit)
            } else {
                Toast.makeText(requireContext(), "Please enter valid interval and select a time unit", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchUserIdAndAddNotification(notificationText: String, interval: Long, timeUnit: String) {
        val url = "http://192.168.254.128/mobdeve/get_user_id.php"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Failed to fetch user ID", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        val jsonResponse = response.body?.string()
                        val userId = jsonResponse?.let {
                            try {
                                JSONObject(it).getInt("user_id")
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (userId != null) {
                            addAutoNotification(userId, notificationText, interval, timeUnit)
                        } else {
                            Toast.makeText(requireContext(), "Error parsing user ID", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error fetching user ID: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun addAutoNotification(userId: Int, notificationText: String, interval: Long, timeUnit: String) {
        val url = "http://192.168.254.128/mobdeve/add_auto_notification.php"
        val formBody = FormBody.Builder()
            .add("user_id", userId.toString())
            .add("notificationText", notificationText)
            .add("interval", interval.toString())
            .add("timeUnit", timeUnit)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Failed to add auto notification", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Auto notification added successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
