package com.example.smartree
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartree.databinding.ActivityInfoSensorBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InfoSensorActivity : AppCompatActivity() {
    lateinit var binding: ActivityInfoSensorBinding
    lateinit var sensorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoSensorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorId = intent.extras?.getString("idSensor", "").toString()
        loadInformation()

        binding.editInfoSensorButton.setOnClickListener{
            changeValues()
        }

        binding.deleteSensorButton.setOnClickListener {
            Firebase.firestore.collection("sensors").document(sensorId).delete()
            val intent = Intent(this, HomeFragment::class.java).apply{
                putExtra("resetSensors", "true")
            }
            startActivity(intent)
        }

       binding.backAuxSensorHeader.setOnClickListener {
           finish()
       }
    }

    private fun changeValues() {

        var state: String = binding.stateSensorText.text.toString()
        if(state.contentEquals ("Activo", true) || state.contentEquals("Inactivo", true)){

            if(binding.nameSensorInfoText.text.toString()!=""){
                Firebase.firestore.collection("sensors").document(sensorId)
                    .update("name",binding.nameSensorInfoText.text.toString())

                Firebase.firestore.collection("sensors").document(sensorId)
                    .update("state",  state.lowercase())

                showMsg("Datos cambiados exitosamente")

                val intent = Intent(this, HomeFragment::class.java).apply{
                    putExtra("resetSensors", "true")
                }
                startActivity(intent)
            }

        }else{
            showMsg("Estado invalido.El sensor solo puede estar activo o inactivo")
        }
    }


    private fun loadInformation() {
        disableTextFields()
        /// Load information from Firestore
        Firebase.firestore.collection("sensors")
            .document(sensorId)
            .get()
            .addOnCompleteListener{ sensor ->
                var sensorInfo: Sensor = sensor.result.toObject(Sensor::class.java)!!
                if(sensorInfo.id !="") {
                    binding.nameSensorInfoText.setText(sensorInfo.name)
                    binding.idSensorInfoText.setText(sensorInfo.id)
                    binding.typeSensorInfoText.setText(sensorInfo.type)
                    binding.coordinatesSensorText.setText(sensorInfo.coordinates)
                    dinamicState(sensorInfo.state)
                }
            }
        }

    private fun disableTextFields(){
        binding.idSensorInfoText.setKeyListener(null)
        binding.typeSensorInfoText.setKeyListener(null)

        binding.idSensorInfoText.setOnClickListener {
            showMsg("No se puede editar el codigo")
        }

        binding.typeSensorInfoText.setOnClickListener {
            showMsg("No se puede editar el tipo de sensor");
        }
    }

    private fun dinamicState(state: String){
        val imageResourceEx = resources.getIdentifier("@drawable/ic_baseline_close_24", null, packageName)
        val imageResourceEye = resources.getIdentifier("@drawable/ic_baseline_remove_red_eye_24", null, packageName)
        binding.stateSensorImg.visibility = View.VISIBLE

        if(state == "inactivo"){
            binding.stateSensorImg.setImageResource(imageResourceEx)
        }else{
            binding.stateSensorImg.setImageResource(imageResourceEye)
        }

        binding.stateSensorText.setText(state)
    }

    private fun showMsg(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}