package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Initially hide the BottomNavigationView and load the LoginFragment
        bottomNavigationView.visibility = BottomNavigationView.GONE
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, LoginFragment())
                .commit()
        }
    }

    fun onLoginSuccess() {
        // Show BottomNavigationView and set up navigation
        bottomNavigationView.visibility = BottomNavigationView.VISIBLE
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_home -> {
                    selectedFragment = SafetyStatusFragment()
                }
                R.id.nav_weather_updates -> {
                    selectedFragment = WeatherFragment()
                }
                R.id.nav_contacts -> {
                    selectedFragment = ContactsFragment()
                }
                R.id.nav_emergency_mode -> {
                    selectedFragment = PanicButtonFragment()
                }
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, selectedFragment)
                    .commit()
            }
            true
        }

        // Set default selection
        bottomNavigationView.selectedItemId = R.id.nav_home
    }
}
