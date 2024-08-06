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
import java.io.IOException

class EditNotificationFragment : Fragment() {

    private lateinit var notificationTextEditText: EditText
    private lateinit var intervalEditText: EditText
    private lateinit var timeUnitSpinner: Spinner
    private lateinit var editButton: Button
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_edit_notifis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationTextEditText = view.findViewById(R.id.notificationTextEditText)
        intervalEditText = view.findViewById(R.id.intervalEditText)
        timeUnitSpinner = view.findViewById(R.id.timeUnitSpinner)
        editButton = view.findViewById(R.id.editButton)

        val notificationId = arguments?.getInt("notificationId") ?: -1

        editButton.setOnClickListener {
            val notificationText = notificationTextEditText.text.toString()
            val interval = intervalEditText.text.toString().toLongOrNull()
            val timeUnit = timeUnitSpinner.selectedItem.toString()

            if (notificationId != -1 && interval != null) {
                editAutoNotification(notificationId, notificationText, interval, timeUnit)
            } else {
                Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editAutoNotification(id: Int, notificationText: String, interval: Long, timeUnit: String) {
        val url = "http://192.168.254.128/mobdeve/edit_auto_notification.php"
        val formBody = FormBody.Builder()
            .add("id", id.toString())
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
                    Toast.makeText(requireContext(), "Failed to edit auto notification", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Auto notification edited successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
