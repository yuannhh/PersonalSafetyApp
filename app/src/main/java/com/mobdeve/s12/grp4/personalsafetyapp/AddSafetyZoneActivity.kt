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

class AddSafetyZoneActivity : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var addressLineEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var countryEditText: EditText
    private lateinit var addButton: Button
    private val client = OkHttpClient()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_add_safety_zone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameEditText = view.findViewById(R.id.nameEditText)
        addressLineEditText = view.findViewById(R.id.addressLineEditText)
        cityEditText = view.findViewById(R.id.cityEditText)
        stateEditText = view.findViewById(R.id.stateEditText)
        countryEditText = view.findViewById(R.id.countryEditText)
        addButton = view.findViewById(R.id.addButton)

        // Initialize SharedPreferences
        sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val addressLine = addressLineEditText.text.toString()
            val city = cityEditText.text.toString()
            val state = stateEditText.text.toString()
            val country = countryEditText.text.toString()

            val userId = sharedPref.getInt("userId", -1)

            if (userId == -1) {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addSafetyZone(userId, name, addressLine, city, state, country)
        }
    }

    private fun addSafetyZone(userId: Int, name: String, addressLine: String, city: String, state: String, country: String) {
        val url = "http://192.168.254.128/mobdeve/add_safety_zone.php"
        val formBody = FormBody.Builder()
            .add("user_id", userId.toString())
            .add("name", name)
            .add("addressLine", addressLine)
            .add("city", city)
            .add("state", state)
            .add("country", country)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Failed to add safety zone", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Safety zone added successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
