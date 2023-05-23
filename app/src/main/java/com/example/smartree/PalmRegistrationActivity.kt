package com.example.smartree


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.R
import com.example.smartree.databinding.ActivityPalmRegistrationBinding
import com.example.smartree.model.Palm
import com.example.smartree.model.Sensor
import com.example.smartree.model.Statistics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class PalmRegistrationActivity : AppCompatActivity() {

    private var lat:Double=0.0
    private var lon:Double=0.0
    private var sensors = ArrayList<String>()
    private var sensorsID = ArrayList<String>()

    private val binding: ActivityPalmRegistrationBinding by lazy {
        ActivityPalmRegistrationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSpinner()

        binding.backBtnRPalm.setOnClickListener {
            finish()
        }

        binding.addPalm.setOnClickListener{ addPalm() }

        binding.cancelAddPalm.setOnClickListener{
            finish()
        }
    }

    private fun addPalm(){
        val uid = Firebase.auth.currentUser!!.uid
        val id = uid +"_PALM_"+UUID.randomUUID()
        val name = binding.namePalmETLayout.editText?.text.toString()
        val place = binding.placePalmET.editText?.text.toString()
        val type = if(binding.radioButton.isChecked){
            "Chontaduro"
        }else{
            "Coco"
        }
        val index = binding.sensorSpinner.selectedItemPosition
        val sensorID = sensorsID[index]
        if(validatePalm(name, place)){
            val palm = Palm(id, uid, name, type, place, sensorID, lat, lon)
            Firebase.firestore.collection("palms").document(id).set(palm)
                .addOnSuccessListener{
                    Firebase.firestore.collection("statistics").document(uid).get().addOnSuccessListener {
                        val stat = it.toObject(Statistics::class.java)
                        val total = stat?.total?.plus(1)
                        Firebase.firestore.collection("sensors").document(palm.sensorID).update("linked", true).addOnSuccessListener {
                            Firebase.firestore.collection("statistics").document(uid).update("total", total).addOnSuccessListener {
                                Toast.makeText(this, "La palma fue agregada correctamente", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "No fue posible agregar la palma", Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(this, "Campos vacios o incompletos", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setSpinner(){
        sensors.add("Selecciona un sensor")
        sensorsID.add("")
        Firebase.firestore.collection("sensors")
            .whereEqualTo("uid",Firebase.auth.currentUser!!.uid)
            .whereEqualTo("linked", false)
            .get().addOnSuccessListener {list->
                for (doc in list) {
                    val sensor = doc.toObject(Sensor::class.java)
                    sensors.add(sensor.name)
                    sensorsID.add(sensor.serie)
                }
                val sensorsAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item,sensors)
                binding.sensorSpinner.adapter = sensorsAdapter
            }
    }

    private fun validatePalm(name:String, place:String):Boolean{
        val validate =
            name.length > 5
        &&  place.length > 5

        return validate
    }
}