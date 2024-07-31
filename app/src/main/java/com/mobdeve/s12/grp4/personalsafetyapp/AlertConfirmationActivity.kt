package com.mobdeve.s12.grp4.personalsafetyapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class AlertConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alert_confirmation)

        val btnConfirm = findViewById<Button>(R.id.btn_confirm)
        val btnCancel = findViewById<Button>(R.id.btn_cancel)

        btnConfirm.setOnClickListener {
            val intent = Intent(this, AlertSentActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}
