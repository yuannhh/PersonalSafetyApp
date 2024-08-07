package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import okhttp3.*
import java.io.IOException

class CreateAccountFragment : Fragment() {

    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.createacc, container, false)

        val fullNameEditText: EditText = view.findViewById(R.id.fullNameEditText)
        val dobEditText: EditText = view.findViewById(R.id.dobEditText)
        val emailEditText: EditText = view.findViewById(R.id.emailEditText)
        val passwordEditText: EditText = view.findViewById(R.id.passwordEditText)
        val registerButton: Button = view.findViewById(R.id.registerButton)
        val backButton: Button = view.findViewById(R.id.backButton)

        registerButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString()
            val dob = dobEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            register(fullName, dob, email, password)
        }

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, LoginFragment())
                .commit()
        }

        return view
    }

    private fun register(fullName: String, dob: String, email: String, password: String) {
        val url = "http://192.168.56.1/mobdeve/register.php" // Use 10.0.2.2 for localhost in Android emulator

        val formBody = FormBody.Builder()
            .add("full_name", fullName)
            .add("date_of_birth", dob)
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("CreateAccountFragment", "Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                requireActivity().runOnUiThread {
                    if (responseData != null && responseData.contains("Account created successfully")) {
                        Toast.makeText(context, "Account created successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment, LoginFragment())
                            .commit()
                    } else {
                        Toast.makeText(context, "Failed to create account", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
