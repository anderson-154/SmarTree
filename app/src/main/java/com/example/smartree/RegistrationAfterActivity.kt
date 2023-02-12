package com.example.smartree

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.smartree.databinding.ActivityRegistrationAfterBinding
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

        binding.finishBtn.setOnClickListener {

            val uid = Firebase.auth.currentUser?.uid.toString()
            val email = Firebase.auth.currentUser?.email.toString()
            val type = binding.tipoET.editText!!.text.toString()
            val document = binding.documentoET.editText!!.text.toString()
            val name = binding.nameET.editText!!.text.toString()
            val phone = binding.phoneET.editText!!.text.toString()
            val address = binding.addressET.editText!!.text.toString()
            val city = binding.cityET.editText!!.text.toString()
            val dpto = binding.dptoET.editText!!.text.toString()

            //Check if are there empty fields
            if(validate(listOf(type, document, name, phone, address, city, dpto))){

                val user = User(uid, email, type, document, name, phone, address, city, dpto);
                registerUserData(user)

            }else{
                showAlert("Por favor llene todos los campos y asegurese de que los campos coincidan")
            }
        }

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
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
            Toast.makeText(this, "Datos registrados exitosamente", Toast.LENGTH_LONG).show()
            val provider = intent.extras?.getString("provider", null)
            showHome(user.email, provider)
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
}