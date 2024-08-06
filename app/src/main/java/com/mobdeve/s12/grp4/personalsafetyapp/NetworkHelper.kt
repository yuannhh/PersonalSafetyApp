package com.mobdeve.s12.grp4.personalsafetyapp

import android.content.Context
import okhttp3.*
import java.io.IOException

class NetworkHelper(private val context: Context) {

    private val client = OkHttpClient()

    fun getContacts(userId: Int, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val url = "http://192.168.254.128/mobdeve/get_contacts.php?user_id=$userId"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                onSuccess(response.body?.string() ?: "No response")
            }
        })
    }

    fun addContact(userId: Int, name: String, phoneNumber: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val url = "http://192.168.254.128/mobdeve/add_contact.php"

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
                onSuccess(response.body?.string() ?: "No response")
            }
        })
    }

    fun deleteContact(id: Int, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val url = "http://192.168.254.128/mobdeve/delete_contact.php"

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
        val url = "http://192.168.254.128/mobdeve/update_contact.php"

        val formBody = FormBody.Builder()
            .add("id", id.toString())
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
                onSuccess(response.body?.string() ?: "No response")
            }
        })
    }
}
