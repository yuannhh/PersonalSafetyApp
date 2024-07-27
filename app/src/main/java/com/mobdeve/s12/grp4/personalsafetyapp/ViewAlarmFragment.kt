package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AlertDialog
import android.widget.TextView
import android.widget.Toast
import com.mobdeve.s12.grp4.personalsafetyapp.databinding.ViewAlarmBinding

class ViewAlarmFragment : Fragment() {

    private var _binding: ViewAlarmBinding? = null
    private val binding get() = _binding!!

    // Use a mutable list for alarms
    private var alarms = mutableListOf(
        "Alarm 1 - High traffic reported on EDSA.",
        "Alarm 2 - Suspicious activity near Park Ave.",
        "Alarm 3 - Accident at Main St. and 2nd Ave.",
        "Alarm 4 - Vehicle breakdown on Highway 50.",
        "Alarm 5 - Road closure on 7th Ave.",
        "Alarm 6 - Flood warning for downtown area."
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ViewAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager = binding.alarmViewPager
        val pagerAdapter = AlarmPagerAdapter()
        viewPager.adapter = pagerAdapter

        binding.viewIncidentsButton.setOnClickListener {
            findNavController().navigate(R.id.action_viewAlarmFragment_to_viewIncidentFragment)
        }

        binding.clearHistoryButton.setOnClickListener {
            showClearHistoryConfirmationDialog()
        }

        binding.imageButton2.setOnClickListener {
            findNavController().navigate(R.id.action_viewAlarmFragment_to_safetyStatusFragment)
        }
    }

    private fun showClearHistoryConfirmationDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Clear History")
            setMessage("Are you sure you want to clear all alarm history? This action cannot be undone.")
            setPositiveButton("Confirm") { _, _ ->
                // Handle clear history logic here
                alarms.clear() // Clear the alarms list
                (binding.alarmViewPager.adapter as AlarmPagerAdapter).notifyDataSetChanged() // Notify adapter of changes
                updateEmptyState() // Update the UI to show "No alarms" message
                Toast.makeText(requireContext(), "Alarm history cleared", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("Cancel", null)
            show()
        }
    }

    private fun updateEmptyState() {
        if (alarms.isEmpty()) {
            binding.alarmViewPager.visibility = View.GONE
            binding.emptyStateTextView.visibility = View.VISIBLE
        } else {
            binding.alarmViewPager.visibility = View.VISIBLE
            binding.emptyStateTextView.visibility = View.GONE
        }
    }

    inner class AlarmPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(container.context)
            val layout = inflater.inflate(R.layout.alarm_page, container, false) as ViewGroup

            val startIndex = position * 3
            val endIndex = minOf(startIndex + 3, alarms.size)

            layout.removeAllViews() // Clear previous views to avoid duplication

            for (i in startIndex until endIndex) {
                val alarmView = LayoutInflater.from(container.context)
                    .inflate(R.layout.alarm_item, layout, false) as TextView
                alarmView.text = alarms[i]
                layout.addView(alarmView)
            }

            container.addView(layout)
            return layout
        }

        override fun getCount(): Int {
            return if (alarms.isEmpty()) 1 else (alarms.size + 2) / 3 // Show one page for "No alarms" message if empty
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
