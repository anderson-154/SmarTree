package com.example.smartree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smartree.databinding.ActivityPalmRegistrationBinding

class PalmRegistrationActivity : AppCompatActivity() {

    private val binding: ActivityPalmRegistrationBinding by lazy {
        ActivityPalmRegistrationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backBtnRPalm.setOnClickListener {
            finish()
        }
    }
}