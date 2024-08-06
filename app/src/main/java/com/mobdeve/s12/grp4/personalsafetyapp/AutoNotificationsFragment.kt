package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class AutoNotificationsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var autoNotificationAdapter: AutoNotificationAdapter
    private lateinit var addButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.auto_notifis, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewNotifications)
        addButton = view.findViewById(R.id.addButton)
        editButton = view.findViewById(R.id.editButton)
        deleteButton = view.findViewById(R.id.deleteButton)

        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", 0)
        val userId = sharedPref.getInt("userId", -1)
        if (userId == -1) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return view
        }

        fetchAutoNotifications(userId)

        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_autoNotificationsFragment_to_addNotificationFragment)
        }

        editButton.setOnClickListener {
            findNavController().navigate(R.id.action_autoNotificationsFragment_to_editNotificationFragment)
        }

        deleteButton.setOnClickListener {
            findNavController().navigate(R.id.action_autoNotificationsFragment_to_deleteNotificationFragment)
        }

        return view
    }

    private fun fetchAutoNotifications(userIdParam: Int) {
        val url = "http://192.168.254.128/mobdeve/auto_notifications.php"

        val formBody = FormBody.Builder()
            .add("user_id", userIdParam.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("AutoNotificationsFragment", "Error: ${e.message}")
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Failed to fetch notifications", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("AutoNotificationsFragment", "Response Data: $responseData")

                requireActivity().runOnUiThread {
                    if (response.isSuccessful && responseData != null) {
                        try {
                            val jsonArray = JSONArray(responseData)
                            val autoNotifications = mutableListOf<AutoNotification>()
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val id = jsonObject.getInt("id")
                                val userId = jsonObject.getInt("user_id") // Consider renaming this variable if needed
                                val notificationText = jsonObject.getString("notification_text")
                                val interval = jsonObject.getLong("interval")
                                val timeUnit = jsonObject.getString("time_unit")
                                autoNotifications.add(AutoNotification(id, userId, notificationText, interval, timeUnit))
                            }
                            autoNotificationAdapter = AutoNotificationAdapter(autoNotifications)
                            recyclerView.adapter = autoNotificationAdapter
                        } catch (e: Exception) {
                            Log.e("AutoNotificationsFragment", "Parsing error: ${e.message}")
                            Toast.makeText(context, "Failed to parse notifications", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "No notifications found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
