package com.example.smartree

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartree.databinding.ActivityInfoSensorBinding
import com.example.smartree.databinding.ActivityLoginBinding
import com.example.smartree.model.Palm
import com.example.smartree.model.Sensor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class InfoSensorActivity : AppCompatActivity() {

    private val binding: ActivityInfoSensorBinding by lazy {
        ActivityInfoSensorBinding.inflate(layoutInflater)
    }

    lateinit var sensorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setup()
        sensorId = intent.extras?.getString("idSensor", "").toString()
        loadSensor()
    }

    private fun loadSensor(){
        Firebase.firestore.collection("sensors").document(sensorId).get()
            .addOnSuccessListener {
                val sensor = it.toObject(Sensor::class.java)!!
                binding.nameSensorETLayout.editText?.setText(sensor.name)
                binding.serieSensorET.editText?.setText(sensor.serie)
                binding.typeSensorET.editText?.setText(sensor.type)
                binding.stateSensorET.editText?.setText(sensor.state)
            }.addOnFailureListener {
                Toast.makeText(this, "Error al cargar la información", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setup(){
        binding.cancelEditSensor.setOnClickListener {
            finish()
        }
        binding.backBtnRSensor.setOnClickListener {
            finish()
        }
        binding.deleteSensorButton.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmar")
                .setMessage("¿Está seguro de que desea realizar esta acción?")
                .setPositiveButton("Sí") { dialog, which ->
                    Firebase.firestore.collection("sensors").document(sensorId).delete().addOnSuccessListener {
                        Firebase.firestore.collection("palms").whereEqualTo("sensorID",sensorId).get()
                            .addOnSuccessListener {list->
                                if(!list.isEmpty){
                                    val palmID = list.documents[0].toObject(Palm::class.java)!!.id
                                    Firebase.firestore.collection("palms").document(palmID).update("sensorID", "").addOnSuccessListener {
                                        Toast.makeText(this, "El sensor se ha desconectado de la palma asociada", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                Toast.makeText(this, "El sensor ha sido eliminado", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                    }
                }
            val alertDialog = builder.create()
            alertDialog.show()
        }
        binding.editSensor.setOnClickListener{
            val name = binding.nameSensorETLayout.editText!!.text.toString()
            if(name.length>5){
                Firebase.firestore.collection("sensors").document(sensorId).update("name", name).addOnSuccessListener {
                    Toast.makeText(this, "El sensor ha sido actualizado", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Nombre demasiado corto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}