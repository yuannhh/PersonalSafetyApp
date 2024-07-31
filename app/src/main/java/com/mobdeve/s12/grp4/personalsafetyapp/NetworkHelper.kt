package com.mobdeve.s12.grp4.personalsafetyapp

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class NetworkHelper(context: Context) {
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    fun addContact(name: String, phoneNumber: String, callback: (String) -> Unit) {
        val url = "http://192.168.254.111/add_contact.php"
        val request = object : StringRequest(Method.POST, url, Response.Listener { response ->
            callback(response)
        }, Response.ErrorListener { error ->
            callback(error.toString())
        }) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf("name" to name, "phone_number" to phoneNumber)
            }
        }
        requestQueue.add(request)
    }

    fun getContacts(callback: (String) -> Unit) {
        val url = "http://192.168.254.111/get_contacts.php"
        val request = StringRequest(Request.Method.GET, url, Response.Listener { response ->
            Log.d("NetworkHelper", "Server response: $response")
            callback(response)
        }, Response.ErrorListener { error ->
            Log.e("NetworkHelper", "Error response: ${error.message}")
            callback(error.toString())
        })
        requestQueue.add(request)
    }

    fun deleteContact(id: Int, callback: (String) -> Unit) {
        val url = "http://192.168.254.111/delete_contact.php"
        val request = object : StringRequest(Method.POST, url, Response.Listener { response ->
            callback(response)
        }, Response.ErrorListener { error ->
            callback(error.toString())
        }) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf("id" to id.toString())
            }
        }
        requestQueue.add(request)
    }

    fun updateContact(id: Int, name: String, phoneNumber: String, callback: (String) -> Unit) {
        val url = "http://192.168.254.111/update_contact.php"
        val request = object : StringRequest(Method.POST, url, Response.Listener { response ->
            callback(response)
        }, Response.ErrorListener { error ->
            callback(error.toString())
        }) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf("id" to id.toString(), "name" to name, "phone_number" to phoneNumber)
            }
        }
        requestQueue.add(request)
    }
}

