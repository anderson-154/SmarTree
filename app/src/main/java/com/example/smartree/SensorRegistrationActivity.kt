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
            var name = binding.nameSensorRegistText.text.toString()
            var id = UUID.randomUUID().toString()

            if (name != "" && id != "" && sensorType!="") {
                var sensor = Sensor(id, name, "", sensorType, "Activo")
                Firebase.firestore.collection("sensors")
                    .document(id)
                    .set(sensor).addOnCompleteListener {
                        Toast.makeText(this, "Datos registrados exitosamente", Toast.LENGTH_SHORT).show()
                    }

                val intent = Intent(this,HomeFragment::class.java).apply{
                    putExtra("newSensor", "true")
                    putExtra("sensor", Gson().toJson(sensor))
                }
                startActivity(intent)
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
        binding.sensorTypeSpinner.setAdapter(arrayAdapter)
    }

}