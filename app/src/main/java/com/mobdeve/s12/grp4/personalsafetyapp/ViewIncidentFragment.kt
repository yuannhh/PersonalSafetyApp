package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AlertDialog
import com.mobdeve.s12.grp4.personalsafetyapp.databinding.ViewIncidentBinding

class ViewIncidentFragment : Fragment() {

    private var _binding: ViewIncidentBinding? = null
    private val binding get() = _binding!!

    // Use a mutable list for incidents
    private var incidents = mutableListOf(
        "Incident 1 - Road crash incident at C5 Ortigas F/O SB involving car and pick-up as of 5:50 PM.",
        "Incident 2 - Stalled wing van due to flat tire at C5 McKinley NB as of 5:11 PM.",
        "Incident 3 - Another incident...",
        "Incident 4 - Another incident...",
        "Incident 5 - Another incident...",
        "Incident 6 - Another incident..."
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ViewIncidentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager = view.findViewById(R.id.incidentViewPager)
        val pagerAdapter = IncidentPagerAdapter()
        viewPager.adapter = pagerAdapter

        binding.viewAlarmsButton.setOnClickListener {
            findNavController().navigate(R.id.action_viewIncidentFragment_to_viewAlarmFragment)
        }

        binding.clearHistoryButton.setOnClickListener {
            showClearHistoryConfirmationDialog()
        }

        binding.imageButton2.setOnClickListener {
            findNavController().navigate(R.id.action_viewIncidentFragment_to_safetyStatusFragment)
        }

        updateEmptyState()
    }

    private fun showClearHistoryConfirmationDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Clear Incident History")
            setMessage("Are you sure you want to clear all incidents?")
            setPositiveButton("Yes") { _, _ ->
                // Handle clear history logic here
                incidents.clear() // Clear the incidents list
                (binding.incidentViewPager.adapter as IncidentPagerAdapter).notifyDataSetChanged() // Notify adapter of changes
                updateEmptyState()
                Toast.makeText(requireContext(), "Incident history cleared", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("No", null)
            show()
        }
    }

    private fun updateEmptyState() {
        if (incidents.isEmpty()) {
            binding.emptyStateTextView.visibility = View.VISIBLE
            binding.incidentViewPager.visibility = View.GONE
        } else {
            binding.emptyStateTextView.visibility = View.GONE
            binding.incidentViewPager.visibility = View.VISIBLE
        }
    }

    inner class IncidentPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(container.context)
            val layout = inflater.inflate(R.layout.incident_page, container, false) as ViewGroup

            val startIndex = position * 3
            val endIndex = minOf(startIndex + 3, incidents.size)

            layout.removeAllViews() // Clear previous views to avoid duplication

            for (i in startIndex until endIndex) {
                val incidentView = LayoutInflater.from(container.context)
                    .inflate(R.layout.incident_item, layout, false) as TextView
                incidentView.text = incidents[i]
                layout.addView(incidentView)
            }

            container.addView(layout)
            return layout
        }

        override fun getCount(): Int {
            return (incidents.size + 2) / 3 // Calculate number of pages needed
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
