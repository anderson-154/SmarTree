package com.example.smartree


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.R
import com.example.smartree.databinding.ActivityPalmEditBinding
import com.example.smartree.model.Palm
import com.example.smartree.model.Sensor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PalmEditActivity : AppCompatActivity() {

    private var lat:Double=0.0
    private var lon:Double=0.0
    private var sensors = ArrayList<String>()
    private var sensorsID = ArrayList<String>()
    private var palm:Palm = Palm()

    private val binding: ActivityPalmEditBinding by lazy {
        ActivityPalmEditBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val id = intent.extras!!.getString("palmID", "")
        loadPalm(id)

        binding.backBtnRPalm.setOnClickListener {
            finish()
        }

        binding.editPalm.setOnClickListener{ editPalm() }

        binding.cancelEditPalm.setOnClickListener{
            finish()
        }
    }

    private fun loadPalm(id:String){
        if(id!="") {
            Firebase.firestore.collection("palms").document(id).get().addOnSuccessListener {
                palm = it.toObject(Palm::class.java)!!
                binding.nameEditPalm.editText!!.setText(palm.name)
                if(palm.type=="Coco"){
                    binding.radioButtonCoco.isChecked = true
                    binding.radioButton3.isChecked = false
                }
                binding.newPlacePalmET.editText!!.setText(palm.place)
                setSpinner(palm.sensorID)
            }.addOnFailureListener {
                Toast.makeText(this, "No se pudo mostrar la información", Toast.LENGTH_SHORT).show()
                finish()
            }
        }else{
            finish()
        }
    }

    private fun editPalm(){
        val name = binding.nameEditPalm.editText?.text.toString()
        val place = binding.newPlacePalmET.editText?.text.toString()
        val type = if(binding.radioButtonCoco.isChecked){
            "Coco"
        }else{
            "Chontaduro"
        }
        val index = binding.sensorSpinner.selectedItemPosition
        val sensorID = sensorsID[index]
        if(validatePalm(name, place)){
            val newPalm = Palm(palm.id, palm.uid, name, type, place, sensorID, lat, lon)
            Firebase.firestore.collection("palms").document(palm.id).set(newPalm)
                .addOnSuccessListener{
                    if(palm.sensorID!="" && newPalm.sensorID!=palm.sensorID){
                        Firebase.firestore.collection("sensors").document(palm.sensorID).update("linked", false).addOnSuccessListener {
                            Toast.makeText(this, "EL sensor anterior fue desenlazado", Toast.LENGTH_SHORT).show()
                        }
                    }else if(palm.sensorID=="" && newPalm.sensorID!=""){
                        Firebase.firestore.collection("sensors").document(newPalm.sensorID).update("linked", true).addOnSuccessListener {
                            Toast.makeText(this, "Nuevo sensor enalazado", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Firebase.firestore.collection("sensors").document(palm.sensorID).update("linked", false).addOnSuccessListener {
                            Toast.makeText(this, "Nuevo sensor enalazado", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Toast.makeText(this, "La palma se actualizó correctamente", Toast.LENGTH_SHORT).show()

                }.addOnFailureListener {
                    Toast.makeText(this, "No fue posible editar la palma", Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(this, "Campos vacios o incompletos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setSpinner(sensorID:String){
        if(sensorID!=""){
            Firebase.firestore.collection("sensors").document(sensorID).get().addOnSuccessListener {
                val sensorName = it.toObject(Sensor::class.java)!!.name
                sensors.add(sensorName)
                sensorsID.add(sensorID)
            }
        }

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
                binding.sensorSpinner.setSelection(1)
            }
    }

    private fun validatePalm(name:String, place:String):Boolean{
        val validate =
            name.length > 5
        &&  place.length > 5

        return validate
    }
}