package com.mobdeve.s12.grp4.personalsafetyapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class PanicButtonFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_panic_button, container, false)

        val btnEmergency = rootView.findViewById<Button>(R.id.btn_emergency)
        btnEmergency.setOnClickListener {
            val intent = Intent(activity, AlertConfirmationActivity::class.java)
            startActivity(intent)
        }

        return rootView
    }
}


