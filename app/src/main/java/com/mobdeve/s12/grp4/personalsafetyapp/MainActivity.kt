package com.mobdeve.s12.grp4.personalsafetyapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if there's a fragment specified in the intent
        val fragment = intent.getStringExtra("fragment")
        if (fragment != null && fragment == "PanicButtonFragment") {
            loadFragment(PanicButtonFragment())
        } else {
            // Initially load the LoginFragment
            if (savedInstanceState == null) {
                loadFragment(LoginFragment())
            }
        }
    }

    fun onLoginSuccess() {
        setContentView(R.layout.activity_main)
        setupBottomNavigation()
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
                    selectedFragment = PanicButtonFragment()
                }
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment)
            }
            true
        }

        // Set default selection
        bottomNavigationView.selectedItemId = R.id.nav_home
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
