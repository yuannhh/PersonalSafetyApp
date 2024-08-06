package com.mobdeve.s12.grp4.personalsafetyapp

import android.app.AlertDialog
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
import org.json.JSONArray
import org.json.JSONException

class ContactsFragment : Fragment() {

    private lateinit var networkHelper: NetworkHelper
    private lateinit var contactsList: LinearLayout
    private val contactsToDelete = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        networkHelper = NetworkHelper(requireContext())
        contactsList = view.findViewById(R.id.contacts_list)

        val btnAddContact = view.findViewById<Button>(R.id.btn_add_contact)
        val btnDeleteContact = view.findViewById<Button>(R.id.btn_delete_contact)
        val btnEditContact = view.findViewById<Button>(R.id.btn_edit_contact)

        btnAddContact.setOnClickListener {
            showAddContactDialog()
        }

        btnDeleteContact.setOnClickListener {
            deleteSelectedContacts()
        }

        btnEditContact.setOnClickListener {
            editSelectedContact()
        }

        fetchContacts()

        return view
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
        networkHelper.addContact(name, phoneNumber, { response ->
            Log.d("ContactsFragment", "addContact response: $response")
            fetchContacts()
        }, { error ->
            Log.e("ContactsFragment", "addContact error: $error")
        })
    }

    private fun deleteSelectedContacts() {
        contactsToDelete.forEach { id ->
            networkHelper.deleteContact(id, { response ->
                Log.d("ContactsFragment", "deleteContact response: $response")
                fetchContacts()
            }, { error ->
                Log.e("ContactsFragment", "deleteContact error: $error")
            })
        }
        contactsToDelete.clear()
    }

    private fun editSelectedContact() {
        // For simplicity, we'll show the first contact in the list for editing.
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
        networkHelper.getContacts({ response ->
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
                        contactView.tag = id  // Store contact id as tag
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
            } catch (e: JSONException) {
                Log.e("ContactsFragment", "Error parsing contacts response", e)
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
            fetchContacts()
        }, { error ->
            Log.e("ContactsFragment", "updateContact error: $error")
        })
    }
}





