package com.mobdeve.s12.grp4.personalsafetyapp

import okhttp3.*
import java.io.IOException

class NetworkHelper(private val context: OkHttpClient) {

    private val client = OkHttpClient()

    fun getContacts(userId: Int, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val url = "http://192.168.1.21/mobdeve/get_contacts.php?user_id=$userId"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    onSuccess(response.body?.string() ?: "No response")
                } else {
                    onFailure("Error: ${response.code}")
                }
            }
        })
    }

    fun addContact(userId: Int, name: String, phoneNumber: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val url = "http://192.168.1.21/mobdeve/add_contact.php"
        val formBody = FormBody.Builder()
            .add("user_id", userId.toString())
            .add("name", name)
            .add("phone_number", phoneNumber)
            .build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string() ?: ""
                if (response.isSuccessful && responseBody.contains("\"success\":true")) {
                    onSuccess(responseBody)
                } else {
                    onFailure("Failed to add contact")
                }
            }
        })
    }

    fun deleteContact(id: Int, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val url = "http://192.168.1.21/mobdeve/delete_contact.php"

        val formBody = FormBody.Builder()
            .add("id", id.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                onSuccess(response.body?.string() ?: "No response")
            }
        })
    }

    fun updateContact(id: Int, name: String, phoneNumber: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val url = "http://192.168.1.21/mobdeve/update_contact.php"
        val formBody = FormBody.Builder()
            .add("id", id.toString())
            .add("contact_name", name)
            .add("contact_phone", phoneNumber)
            .build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string() ?: ""
                if (response.isSuccessful && responseBody.contains("\"success\":true")) {
                    onSuccess(responseBody)
                } else {
                    onFailure("Failed to update contact")
                }
            }
        })
    }
}
