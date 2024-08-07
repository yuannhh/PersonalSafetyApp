package com.mobdeve.s12.grp4.personalsafetyapp

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class ContactsFragment : Fragment() {

    private lateinit var networkHelper: NetworkHelper
    private lateinit var contactsList: LinearLayout
    private val contactsToDelete = mutableListOf<Int>()
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        networkHelper = NetworkHelper(requireContext())
        contactsList = view.findViewById(R.id.contacts_list)

        val btnAddContact = view.findViewById<Button>(R.id.btn_add_contact)
        val btnDeleteContact = view.findViewById<Button>(R.id.btn_add_contact)
        val btnEditContact = view.findViewById<Button>(R.id.btn_add_contact)

        btnAddContact.setOnClickListener {
            showAddContactDialog()
        }

        btnDeleteContact.setOnClickListener {
            deleteSelectedContacts()
        }

        btnEditContact.setOnClickListener {
            editSelectedContact()
        }

        userId = getLoggedInUserId()
        fetchContacts()

        return view
    }

    private fun getLoggedInUserId(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", -1)
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

    private fun addContact(name: String, phoneNumber: String) {
        networkHelper.addContact(userId, name, phoneNumber, { response ->
            Log.d("ContactsFragment", "addContact response: $response")
            if (response == "success") {
                fetchContacts()
            } else {
                Log.e("ContactsFragment", "Failed to add contact")
            }
        }, { error ->
            Log.e("ContactsFragment", "addContact error: $error")
        })
    }

    private fun deleteSelectedContacts() {
        contactsToDelete.forEach { id ->
            networkHelper.deleteContact(id, { response ->
                Log.d("ContactsFragment", "deleteContact response: $response")
                if (response == "success") {
                    fetchContacts()
                } else {
                    Log.e("ContactsFragment", "Failed to delete contact")
                }
            }, { error ->
                Log.e("ContactsFragment", "deleteContact error: $error")
            })
        }
        contactsToDelete.clear()
    }

    private fun editSelectedContact() {
        if (contactsList.childCount > 0) {
            val firstContactView = contactsList.getChildAt(0)
            val nameTextView = firstContactView.findViewById<TextView>(R.id.contact_name)
            val phoneTextView = firstContactView.findViewById<TextView>(R.id.contact_phone)
            val name = nameTextView.text.toString()
            val phoneNumber = phoneTextView.text.toString()
            val id = firstContactView.tag as Int

            showUpdateContactDialog(id, name, phoneNumber)
        }
    }

    private fun fetchContacts() {
        networkHelper.getContacts(userId, { response ->
            requireActivity().runOnUiThread {
                try {
                    contactsList.removeAllViews()
                    val lines = response.split("\n")
                    for (line in lines) {
                        val parts = line.split("|")
                        if (parts.size == 3) {
                            val id = parts[0].toInt()
                            val name = parts[1]
                            val phoneNumber = parts[2]

                            val contactView = LayoutInflater.from(requireContext()).inflate(R.layout.contact_item, null)
                            contactView.tag = id
                            val nameTextView = contactView.findViewById<TextView>(R.id.contact_name)
                            val phoneTextView = contactView.findViewById<TextView>(R.id.contact_phone)
                            val deleteButton = contactView.findViewById<Button>(R.id.btn_delete_contact)
                            val updateButton = contactView.findViewById<Button>(R.id.btn_update_contact)

                            nameTextView.text = name
                            phoneTextView.text = phoneNumber

                            deleteButton.setOnClickListener {
                                contactsToDelete.add(id)
                                contactView.visibility = View.GONE
                            }

                            updateButton.setOnClickListener {
                                showUpdateContactDialog(id, name, phoneNumber)
                            }

                            contactsList.addView(contactView)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ContactsFragment", "Error parsing contacts response", e)
                }
            }
        }, { error ->
            Log.e("ContactsFragment", "fetchContacts error: $error")
        })
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
                updateContact(id, updatedName, updatedPhoneNumber)
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun updateContact(id: Int, name: String, phoneNumber: String) {
        networkHelper.updateContact(id, name, phoneNumber, { response ->
            Log.d("ContactsFragment", "updateContact response: $response")
            if (response == "success") {
                fetchContacts()
            } else {
                Log.e("ContactsFragment", "Failed to update contact")
            }
        }, { error ->
            Log.e("ContactsFragment", "updateContact error: $error")
        })
    }
}
