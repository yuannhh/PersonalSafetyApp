package com.mobdeve.s12.grp4.personalsafetyapp

import android.content.Context
import android.content.SharedPreferences
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

class DeleteSafetyZoneActivity : Fragment() {

    private lateinit var idEditText: EditText
    private lateinit var deleteButton: Button
    private val client = OkHttpClient()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_delete_safety_zone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idEditText = view.findViewById(R.id.idEditText)
        deleteButton = view.findViewById(R.id.deleteButton)

        // Initialize SharedPreferences
        sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        deleteButton.setOnClickListener {
            val id = idEditText.text.toString().toIntOrNull()
            if (id == null) {
                Toast.makeText(requireContext(), "Invalid ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = sharedPref.getInt("userId", -1)

            if (userId == -1) {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            deleteSafetyZone(id, userId)
        }
    }

    private fun deleteSafetyZone(id: Int, userId: Int) {
        val url = "http://192.168.254.128/mobdeve/delete_safety_zone.php"
        val formBody = FormBody.Builder()
            .add("id", id.toString())
            .add("user_id", userId.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Failed to delete safety zone", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Safety zone deleted successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
