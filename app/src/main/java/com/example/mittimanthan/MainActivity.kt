package com.example.mittimanthan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.nav_view)
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    loadFragment(Dashboard())
                    true
                }
                R.id.nav_schemes -> {
                    loadFragment(GovernmentSchemes())
                    true
                }
                R.id.nav_prediction -> {
                    loadFragment(CropPrediction())
                    true
                }
                R.id.nav_organic -> {
                    loadFragment(OrganicFarming())
                    true
                }
                R.id.nav_market -> {
                    loadFragment(Market())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_home
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}


