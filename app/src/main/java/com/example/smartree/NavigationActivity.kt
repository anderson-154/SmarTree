package com.example.smartree

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.smartree.databinding.ActivityNavigationBinding

class NavigationActivity : AppCompatActivity() {

    private lateinit var sensorsFragment: SensorsFragment

    private val binding: ActivityNavigationBinding by lazy {
        ActivityNavigationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sensorsFragment = SensorsFragment.newInstance()

        showFragment(sensorsFragment)

        binding.navigator.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.sensorsmenu -> { showFragment(sensorsFragment) }

            }
            true
        }

        binding.profileButton.setOnClickListener{
            val intent = Intent(this, ProfileActivity :: class.java)
            startActivity(intent)
        }
    }

    private fun showFragment (fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.homeFragment.id, fragment)
        transaction.commit()
    }
}