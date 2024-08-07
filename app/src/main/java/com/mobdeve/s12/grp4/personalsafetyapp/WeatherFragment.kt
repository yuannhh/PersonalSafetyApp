package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class WeatherFragment : Fragment() {

    private lateinit var weatherTextView: TextView
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        weatherTextView = view.findViewById(R.id.weather_text_view)

        fetchWeatherAlerts()

        return view
    }

    private fun fetchWeatherAlerts() {
        // Using a sample JSON from a GitHub Gist for demonstration purposes
        val url = "https://gist.githubusercontent.com/johndoe/abcdef123456/raw/sample_weather_alerts.json"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("WeatherFragment", "Failed to fetch weather alerts", e)
                requireActivity().runOnUiThread {
                    weatherTextView.text = "Failed to fetch weather alerts"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    requireActivity().runOnUiThread {
                        weatherTextView.text = "Failed to fetch weather alerts: ${response.code}"
                    }
                    return
                }

                response.body?.let {
                    val responseData = it.string()
                    Log.d("WeatherFragment", "Weather alerts response: $responseData")

                    // Parse the JSON response
                    val weatherAlerts = parseWeatherAlerts(responseData)

                    // Update the UI
                    requireActivity().runOnUiThread {
                        weatherTextView.text = weatherAlerts
                    }
                } ?: run {
                    requireActivity().runOnUiThread {
                        weatherTextView.text = "Failed to fetch weather alerts"
                    }
                }
            }
        })
    }

    private fun parseWeatherAlerts(responseData: String): String {
        return try {
            val jsonObject = JSONObject(responseData)
            val alertsArray = jsonObject.getJSONArray("alerts")
            val alerts = StringBuilder()

            for (i in 0 until alertsArray.length()) {
                val alert = alertsArray.getJSONObject(i)
                alerts.append(alert.getString("description")).append("\n\n")
            }

            alerts.toString()
        } catch (e: Exception) {
            Log.e("WeatherFragment", "Error parsing weather alerts", e)
            "Error parsing weather alerts"
        }
    }
}