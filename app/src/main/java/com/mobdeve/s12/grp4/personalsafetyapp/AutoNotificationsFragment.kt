package com.mobdeve.s12.grp4.personalsafetyapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AutoNotificationsFragment : Fragment() {

    private lateinit var addNewZoneButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var imageButton2: Button
    private val alarmManager: AlarmManager by lazy {
        requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val notificationsList = mutableListOf<Pair<String, Long>>() // List of notifications with intervals

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.auto_notifs, container, false)

        addNewZoneButton = view.findViewById(R.id.addNewZoneButton)
        editButton = view.findViewById(R.id.editButton)
        deleteButton = view.findViewById(R.id.deleteButton)

        addNewZoneButton.setOnClickListener {
            showAddNotificationDialog()
        }

        editButton.setOnClickListener {
            showEditNotificationDialog()
        }

        deleteButton.setOnClickListener {
            showDeleteNotificationDialog()
        }

        imageButton2.setOnClickListener {
            findNavController().navigate(R.id.action_autoNotificationsFragment_to_safetyStatusFragment)
        }

        return view
    }

    private fun showAddNotificationDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.add_notifications, null)
        val notificationEditText = view.findViewById<EditText>(R.id.notificationEditText)
        val intervalEditText = view.findViewById<EditText>(R.id.intervalEditText)
        val timeUnitSpinner = view.findViewById<Spinner>(R.id.timeUnitSpinner)

        builder.setView(view)
            .setTitle("Add Notification")
            .setPositiveButton("Add") { dialog, _ ->
                val notificationText = notificationEditText.text.toString()
                val intervalText = intervalEditText.text.toString()
                val timeUnit = timeUnitSpinner.selectedItem.toString()

                if (notificationText.isNotEmpty() && intervalText.isNotEmpty()) {
                    val interval = intervalText.toLong()
                    val timeInMillis = convertToMillis(interval, timeUnit)
                    notificationsList.add(notificationText to timeInMillis)
                    scheduleNotification(notificationText, timeInMillis)
                    Toast.makeText(requireContext(), "Notification Scheduled", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
        builder.create().show()
    }

    private fun showEditNotificationDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.edit_notifications, null)
        val editNotificationEditText = view.findViewById<EditText>(R.id.editNotificationEditText)
        val editIntervalEditText = view.findViewById<EditText>(R.id.editIntervalEditText)
        val editTimeUnitSpinner = view.findViewById<Spinner>(R.id.editTimeUnitSpinner)

        builder.setView(view)
            .setTitle("Edit Notification")
            .setPositiveButton("Save") { dialog, _ ->
                val newNotificationText = editNotificationEditText.text.toString()
                val newIntervalText = editIntervalEditText.text.toString()
                val newTimeUnit = editTimeUnitSpinner.selectedItem.toString()

                if (newNotificationText.isNotEmpty() && newIntervalText.isNotEmpty()) {
                    val newInterval = newIntervalText.toLong()
                    val newTimeInMillis = convertToMillis(newInterval, newTimeUnit)

                    if (notificationsList.isNotEmpty()) {
                        val oldNotification = notificationsList[0]
                        // Cancel the old notification
                        val oldIntent = Intent(requireContext(), NotificationReceiver::class.java).apply {
                            putExtra("notificationText", oldNotification.first)
                        }
                        val oldPendingIntent = PendingIntent.getBroadcast(
                            requireContext(),
                            0,
                            oldIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        alarmManager.cancel(oldPendingIntent)

                        // Update the notification list and schedule the new notification
                        notificationsList[0] = newNotificationText to newTimeInMillis
                        scheduleNotification(newNotificationText, newTimeInMillis)
                        Toast.makeText(requireContext(), "Notification Updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "No notifications to edit", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
        builder.create().show()
    }


    private fun showDeleteNotificationDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.delete_notifications, null)
        val notificationsListView = view.findViewById<ListView>(R.id.notificationsListView)
        val deleteSelectedButton = view.findViewById<Button>(R.id.deleteSelectedButton)

        val notifications = notificationsList.map { it.first } // Replace with real notifications list
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_multiple_choice, notifications)
        notificationsListView.adapter = adapter
        notificationsListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        deleteSelectedButton.setOnClickListener {
            val checkedItems = notificationsListView.checkedItemPositions
            val itemsToDelete = mutableListOf<String>()

            for (i in 0 until checkedItems.size()) {
                if (checkedItems.valueAt(i)) {
                    val position = checkedItems.keyAt(i)
                    itemsToDelete.add(notifications[position])
                }
            }

            if (itemsToDelete.isNotEmpty()) {
                deleteNotifications(itemsToDelete)
                Toast.makeText(requireContext(), "Notifications Deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "No notifications selected", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setView(view)
            .setTitle("Delete Notifications")
            .setNegativeButton("Cancel", null)
        builder.create().show()
    }

    private fun convertToMillis(interval: Long, timeUnit: String): Long {
        return when (timeUnit) {
            "Minutes" -> interval * 60 * 1000
            "Hours" -> interval * 60 * 60 * 1000
            else -> interval * 1000 // Default to seconds
        }
    }

    private fun scheduleNotification(notificationText: String, delayInMillis: Long) {
        val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
            putExtra("notificationText", notificationText)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerTime = System.currentTimeMillis() + delayInMillis
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

    private fun deleteNotifications(notificationsToDelete: List<String>) {
        notificationsToDelete.forEach { notificationText ->
            notificationsList.removeAll { it.first == notificationText }
            val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
                putExtra("notificationText", notificationText)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}
