package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import android.util.Log
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
        bottomNavigationView.setOnItemSelectedListener { item ->
            Log.d("MainActivity", "Navigation item selected: ${item.itemId}")
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
            }
//            if (selectedFragment != null) {
//                Log.d("MainActivity", "Replacing fragment: ${selectedFragment.javaClass.simpleName}")
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.nav_host_fragment, selectedFragment)
//                    .commit()
//            }
            true
        }

        // Set default selection
        bottomNavigationView.selectedItemId = R.id.nav_home
    }
}
