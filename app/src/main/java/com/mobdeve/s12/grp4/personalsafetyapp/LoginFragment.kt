package com.mobdeve.s12.grp4.personalsafetyapp

import android.content.Context
import android.content.SharedPreferences
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

class LoginFragment : Fragment() {

    private val client = OkHttpClient()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login, container, false)

        val emailEditText: EditText = view.findViewById(R.id.emailEditText)
        val passwordEditText: EditText = view.findViewById(R.id.passwordEditText)
        val loginButton: Button = view.findViewById(R.id.loginButton)
        val registerButton: Button = view.findViewById(R.id.registerButton)

        sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            login(email, password)
        }

        registerButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, CreateAccountFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun login(email: String, password: String) {
        val url = "http://192.168.254.128/mobdeve/login.php" // Use the IP address of your computer

        val formBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("LoginFragment", "Error: ${e.message}")
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Network error. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                requireActivity().runOnUiThread {
                    if (responseData != null) {
                        when {
                            responseData.contains("success") -> {
                                val parts = responseData.split(":")
                                if (parts.size == 2) {
                                    try {
                                        val userId = parts[1].toInt()
                                        // Save userId in SharedPreferences
                                        with(sharedPref.edit()) {
                                            putInt("userId", userId)
                                            apply()
                                        }
                                        // Notify MainActivity with user_id
                                        (activity as? MainActivity)?.onLoginSuccess()
                                    } catch (e: NumberFormatException) {
                                        Toast.makeText(context, "An unknown error occurred", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "An unknown error occurred", Toast.LENGTH_SHORT).show()
                                }
                            }
                            responseData.contains("Email does not exist") -> {
                                Toast.makeText(context, "Email does not exist", Toast.LENGTH_SHORT).show()
                            }
                            responseData.contains("Invalid email or password") -> {
                                Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(context, "An unknown error occurred", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "No response from server", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
