package com.example.smartree


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.R
import com.example.smartree.databinding.ActivityPalmRegistrationBinding
import com.example.smartree.model.Palm
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.util.UUID

class PalmRegistrationActivity : AppCompatActivity() {

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onResultLocationSelected)
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

        binding.locationBtn.setOnClickListener{
            val intent = Intent(this, LocationActivity::class.java)
            launcher.launch(intent)
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
        Log.e("--------------->", binding.radioGroup.checkedRadioButtonId.toString())
        val type = if(binding.radioButton.isChecked){
            "Chontaduro"
        }else{
            "Coco"
        }

        val index = binding.sensorSpinner.selectedItemPosition
        val sensorID = if(index!=-1){
            sensorsID[index]
        }else{
            ""
        }
        if(validatePalm(name, place)){
            val palm = Palm(id, uid, name, type, place, sensorID, lat, lon )
            Firebase.firestore.collection("palms").document(id).set(palm)
                .addOnSuccessListener{
                    val intent = Intent(this, NavigationActivity::class.java)
                    val gson = Gson()
                    intent.putExtra("palm", gson.toJson(palm))
                    setResult(RESULT_OK, intent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "No fue posible agregar la palma", Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(this, "Campos vacios o incompletos", Toast.LENGTH_SHORT).show()
        }

    }

    private fun onResultLocationSelected(result:ActivityResult){
        if(result.resultCode== RESULT_OK){
            lat = result.data?.extras?.getDouble("lat")!!
            lon = result.data?.extras?.getDouble("lon")!!
            val coordinates = lat.toString() + lon
            binding.coordinatesTV.text = coordinates
        }else{
            Toast.makeText(this, "Location not selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setSpinner(){
        Firebase.firestore.collection("sensors")
            .whereEqualTo("uid",Firebase.auth.currentUser!!.uid)
            .get().addOnSuccessListener {list->
                for (doc in list) {
                    val sensor = doc.toObject(Palm::class.java)
                    sensors.add(sensor.name)
                    sensorsID.add(sensor.id)
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