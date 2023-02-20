package com.example.smartree

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.smartree.databinding.ActivitySensorRegistrationBinding
import com.example.smartree.model.Sensor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.util.*

class SensorRegistrationActivity : AppCompatActivity() {

    lateinit var binding: ActivitySensorRegistrationBinding
    var sensorType: String =""
    lateinit var sensorTypes: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadSpinner()
        binding.registSensorButton.setOnClickListener {
            val uid = Firebase.auth.currentUser!!.uid
            val name = binding.nameSensorRegistText.text.toString()
            val serie = binding.serieET.editText?.text.toString()

            if (name != "" && serie != "" && sensorType!="") {
                val sensor = Sensor(serie, uid, name, sensorType, "Inactivo")
                Firebase.firestore.collection("sensors")
                    .document(serie)
                    .set(sensor).addOnSuccessListener {
                        Toast.makeText(this, "Datos registrados exitosamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "No fue posible registrar el sensor", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Por favor, rellenar correctamente los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.sensorTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                sensorType = sensorTypes[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
        }

        binding.backBtnRDevice.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("ResourceType")
    private fun loadSpinner(){
        sensorTypes = resources.getStringArray(R.array.sensors_type)
        val arrayAdapter = object: ArrayAdapter<String>(this, R.layout.drop_down_sensors, sensorTypes){

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                //set the color of first item in the drop down list to gray
                if(position == 0) {
                    view.setTextColor(Color.GRAY)
                }else{
                    view.setTextColor(Color.BLACK)
                }
                return view
            }
        }
        binding.sensorTypeSpinner.adapter = arrayAdapter
    }

}