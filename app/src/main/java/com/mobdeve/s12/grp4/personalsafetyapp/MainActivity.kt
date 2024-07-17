package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            // Perform login validation here
            setContentView(R.layout.activity_main)
            setupBottomNavigation()
        }
    }
    
    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_home -> {
                    selectedFragment = HomeFragment()
                }
                R.id.nav_weather_updates -> {
                    selectedFragment = WeatherFragment()
                }
                R.id.nav_contacts -> {
                    selectedFragment = ContactsFragment()
                }
                R.id.nav_offline_mode -> {
                    selectedFragment = OfflineFragment()
                }
                R.id.nav_emergency_mode -> {
                    selectedFragment = EmergencyFragment()
                }

                // Add other fragments similarly
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
            }
            true
        }

        // Set default selection
        bottomNavigationView.selectedItemId = R.id.nav_home
    }
}
