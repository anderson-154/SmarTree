package com.example.smartree


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import com.example.smartree.databinding.ActivityPalmRegistrationBinding
import com.google.type.LatLng

class PalmRegistrationActivity : AppCompatActivity() {

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onResultLocationSelected)
    private var lat:Double=0.0
    private var lon:Double=0.0

    private val binding: ActivityPalmRegistrationBinding by lazy {
        ActivityPalmRegistrationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backBtnRPalm.setOnClickListener {
            finish()
        }

        binding.locationBtn.setOnClickListener{
            val intent = Intent(this, LocationActivity::class.java)
            launcher.launch(intent)
        }
    }

    private fun onResultLocationSelected(result:ActivityResult){
        if(result.resultCode== RESULT_OK){
            lat = result.data?.extras?.getDouble("lat", 0.506)!!
            lon = result.data?.extras?.getDouble("lon", 0.723)!!
            val coordinates = lat.toString() + lon
            binding.coordinatesTV.text = coordinates
        }else{
            Toast.makeText(this, "Location not selected", Toast.LENGTH_SHORT).show()
        }
    }
}