package com.mobdeve.s12.grp4.personalsafetyapp

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.json.JSONArray
import org.json.JSONException

class ContactsFragment : Fragment() {

    private lateinit var networkHelper: NetworkHelper
    private lateinit var contactsList: LinearLayout
    private val contactsToUpdate = mutableListOf<Pair<Int, Pair<String, String>>>()
    private val contactsToDelete = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_contacts, container, false)

        networkHelper = NetworkHelper(requireContext())
        contactsList = rootView.findViewById(R.id.contacts_list)

        val btnAddContact = rootView.findViewById<Button>(R.id.btn_add_contact)
        val btnSaveChanges = rootView.findViewById<Button>(R.id.btn_save_changes)

        btnAddContact.setOnClickListener {
            showAddContactDialog()
        }

        btnSaveChanges.setOnClickListener {
            saveChanges()
        }

        fetchContacts()
        return rootView
    }

    private fun showAddContactDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_contact, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Contact")
            .setView(dialogView)
            .setPositiveButton("Add") { dialogInterface, _ ->
                val nameEditText = dialogView.findViewById<EditText>(R.id.edit_text_name)
                val phoneNumberEditText = dialogView.findViewById<EditText>(R.id.edit_text_phone_number)
                val name = nameEditText.text.toString()
                val phoneNumber = phoneNumberEditText.text.toString()
                addContact(name, phoneNumber)
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun showUpdateContactDialog(id: Int, name: String, phoneNumber: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_contact, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_text_name)
        val phoneNumberEditText = dialogView.findViewById<EditText>(R.id.edit_text_phone_number)
        nameEditText.setText(name)
        phoneNumberEditText.setText(phoneNumber)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Update Contact")
            .setView(dialogView)
            .setPositiveButton("Update") { dialogInterface, _ ->
                val updatedName = nameEditText.text.toString()
                val updatedPhoneNumber = phoneNumberEditText.text.toString()
                contactsToUpdate.add(Pair(id, Pair(updatedName, updatedPhoneNumber)))
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun addContact(name: String, phoneNumber: String) {
        networkHelper.addContact(name, phoneNumber) { response ->
            fetchContacts()
        }
    }

    private fun updateContact(id: Int, name: String, phoneNumber: String) {
        networkHelper.updateContact(id, name, phoneNumber) { response ->
            fetchContacts()
        }
    }

    private fun deleteContact(id: Int) {
        contactsToDelete.add(id)
    }

    private fun saveChanges() {
        contactsToDelete.forEach { id ->
            networkHelper.deleteContact(id) { response ->
                // Handle response if needed
            }
        }
        contactsToDelete.clear()

        contactsToUpdate.forEach { (id, contact) ->
            val (name, phoneNumber) = contact
            networkHelper.updateContact(id, name, phoneNumber) { response ->
                // Handle response if needed
            }
        }
        contactsToUpdate.clear()

        fetchContacts()
    }

    private fun fetchContacts() {
        networkHelper.getContacts { response ->
            try {
                val contactsArray = JSONArray(response)
                requireActivity().runOnUiThread {
                    contactsList.removeAllViews()
                    for (i in 0 until contactsArray.length()) {
                        val contact = contactsArray.getJSONObject(i)
                        val id = contact.getInt("id")
                        val name = contact.getString("name")
                        val phoneNumber = contact.getString("phone_number")

                        val contactView = LayoutInflater.from(requireContext()).inflate(R.layout.contact_item, null)
                        val nameTextView = contactView.findViewById<TextView>(R.id.contact_name)
                        val phoneTextView = contactView.findViewById<TextView>(R.id.contact_phone)
                        val deleteButton = contactView.findViewById<Button>(R.id.btn_delete_contact)
                        val updateButton = contactView.findViewById<Button>(R.id.btn_update_contact)

                        nameTextView.text = name
                        phoneTextView.text = phoneNumber

                        deleteButton.setOnClickListener {
                            deleteContact(id)
                            contactView.visibility = View.GONE
                        }

                        updateButton.setOnClickListener {
                            showUpdateContactDialog(id, name, phoneNumber)
                        }

                        contactsList.addView(contactView)
                    }
                }
            } catch (e: JSONException) {
                Log.e("ContactsFragment", "JSON parsing error: ${e.message}")
                Log.e("ContactsFragment", "Response: $response")
            }
        }
    }
}



