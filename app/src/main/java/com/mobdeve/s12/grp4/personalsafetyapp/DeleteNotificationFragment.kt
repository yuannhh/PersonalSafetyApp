package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import okhttp3.*
import java.io.IOException

class DeleteNotificationFragment : Fragment() {

    private lateinit var idEditText: EditText
    private lateinit var deleteButton: Button
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_delete_notifis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idEditText = view.findViewById(R.id.idEditText)
        deleteButton = view.findViewById(R.id.deleteButton)

        deleteButton.setOnClickListener {
            val id = idEditText.text.toString().toIntOrNull()
            if (id != null) {
                deleteAutoNotification(id)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteAutoNotification(id: Int) {
        val url = "http://192.168.254.128/mobdeve/delete_auto_notification.php"
        val formBody = FormBody.Builder()
            .add("id", id.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Failed to delete auto notification", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Auto notification deleted successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
