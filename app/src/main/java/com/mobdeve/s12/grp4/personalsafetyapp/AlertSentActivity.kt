package com.mobdeve.s12.grp4.personalsafetyapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class AlertSentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alert_sent)

        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener {
            navigateToPanicButtonFragment()
        }
    }

    override fun onBackPressed() {
        navigateToPanicButtonFragment()
    }

    private fun navigateToPanicButtonFragment() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("fragment", "PanicButtonFragment")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}





