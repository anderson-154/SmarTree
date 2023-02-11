package com.example.smartree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.smartree.databinding.ActivitySensorRegistrationBinding

class SensorRegistrationActivity : AppCompatActivity() {

    private val binding: ActivitySensorRegistrationBinding by lazy {
        ActivitySensorRegistrationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backBtnRDevice.setOnClickListener {
            finish()
        }

        var types = ArrayList<String>()
        types.add("Modo de dispositivo")
        types.add("Gateway")
        types.add("Sensor")

        var spinnerAdapter = ArrayAdapter<String>(this,androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,types)
        binding.spinnerType.adapter = spinnerAdapter

    }
}