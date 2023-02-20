package com.example.smartree

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.R
import androidx.appcompat.app.AlertDialog
import com.example.smartree.databinding.ActivityRegistrationAfterBinding
import com.example.smartree.model.Statistics
import com.example.smartree.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistrationAfterActivity : AppCompatActivity() {

    private val binding: ActivityRegistrationAfterBinding by lazy {
        ActivityRegistrationAfterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSpinners()

        binding.finishBtn.setOnClickListener {

            val uid = Firebase.auth.currentUser?.uid.toString()
            val email = Firebase.auth.currentUser?.email.toString()
            val type = binding.spinnerTipoDoc.selectedItem.toString()
            val document = binding.documentoET.editText!!.text.toString()
            val name = binding.nameET.editText!!.text.toString()
            val lastName = binding.lastNameET.editText!!.text.toString()
            val phone = binding.phoneET.editText!!.text.toString()
            val address = binding.addressET.editText!!.text.toString()
            val city = binding.cityET.editText!!.text.toString()
            val dpto = binding.spinnerDpto.selectedItem.toString()

            //Check if are there empty fields
            if((validate(listOf(document, name,lastName, phone, address, city))) && !(type == "Tipo de documento" && (dpto == "Departamento"))){

                val user = User(uid, email, type, document, name, lastName,phone, address, city, dpto);
                registerUserData(user)

            }else{
                showAlert("Por favor llene todos los campos y asegurese de que los campos coincidan")
            }
        }
    }

    private fun validate(data:List<String>): Boolean {
        var isValid = true;
        for(element in data){
            if(element==""){
                isValid=false
                break
            }
        }
        return isValid
    }

    private fun registerUserData(user:User) {
        Firebase.firestore.collection("users").document(user.id).set(user).addOnSuccessListener {
            Firebase.firestore.collection("statistics").document(user.id).set(Statistics()).addOnSuccessListener {
                Toast.makeText(this, "Datos registrados exitosamente", Toast.LENGTH_LONG).show()
                val provider = intent.extras?.getString("provider", null)
                showHome(user.email, provider)
            }
        }.addOnCanceledListener {
            showAlert()
        }
    }

    private fun showAlert(msg:String="Se ha producido un error autenticando al usuario"){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showHome(email:String, provider:String? ){
        val intent = Intent(this, NavigationActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("provider", provider)
        startActivity(intent)
        finish()
    }

    private fun setSpinners(){
        val types = ArrayList<String>()
        types.add("Tipo de documento")
        types.add("C.C")
        types.add("PASAPORTE")

        val spinnerTypeDocAdapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,types)

        binding.spinnerTipoDoc.adapter = spinnerTypeDocAdapter

        val dptos = ArrayList<String>()
        dptos.add("Departamento")
        dptos.add("Amazonas")
        dptos.add("Antioquia")
        dptos.add("Arauca")
        dptos.add("Atlántico")
        dptos.add("Bolívar")
        dptos.add("Boyacá")
        dptos.add("Caldas")
        dptos.add("Caquetá")
        dptos.add("Casanare")
        dptos.add("Cauca")
        dptos.add("Cesar")
        dptos.add("Chocó")
        dptos.add("Córdoba")
        dptos.add("Cundinamarca")
        dptos.add("Guainía")
        dptos.add("Guaviare")
        dptos.add("Huila")
        dptos.add("La Guajira")
        dptos.add("Magdalena")
        dptos.add("Meta")
        dptos.add("Nariño")
        dptos.add("Norte de Santander")
        dptos.add("Putumayo")
        dptos.add("Quindío")
        dptos.add("Risaralda")
        dptos.add("Santander")
        dptos.add("Sucre")
        dptos.add("Tolima")
        dptos.add("Valle del Cauca")
        dptos.add("Vaupés")
        dptos.add("Vichada")

        val spinnerDptosAdapter = ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,dptos)

        binding.spinnerDpto.adapter = spinnerDptosAdapter
    }
}