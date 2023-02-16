package com.example.smartree

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.smartree.databinding.ActivityRegistrationBinding
import com.example.smartree.model.Statistics
import com.example.smartree.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {

    private val binding: ActivityRegistrationBinding by lazy {
        ActivityRegistrationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.nextBtn.setOnClickListener {

            //Check if this account exists
            val email = binding.emailSignUpET.editText!!.text.toString()
            val pass = binding.passwordSignUpET.editText!!.text.toString()

            //Check if are there empty fields
            if(validate(email, pass, binding.passwordConfirmET.editText!!.text.toString())){

                Firebase.auth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener {
                    Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_LONG).show()
                    Firebase.auth.currentUser?.sendEmailVerification()
                    registerUserData()
                }.addOnFailureListener {
                    showAlert()
                }
            }else{
                showAlert("Por favor llene todos los campos y asegurese de que los campos coincidan")
            }
        }

        binding.nextBtn.setOnClickListener {
            startActivity(Intent(this, RegistrationAfterActivity::class.java))
            finish()
        }

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        binding.signInTxt.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }

    private fun validate(email:String, password1:String, password2:String): Boolean {
        val isValid =

        //Conditions
           email != ""
        && password1.length > 8
        && password2 == password1

        return isValid
    }

    private fun registerUserData() {
        val uid = Firebase.auth.currentUser?.uid
        uid?.let {
            val user = User(
                it,
                Firebase.auth.currentUser?.email.toString(),
            )
            Firebase.firestore.collection("users").document(it).set(user).addOnSuccessListener {
                startActivity(Intent(this, LoginActivity::class.java).apply {
                    putExtra("alert","Cuenta creada exitosamente. Verifique su cuenta en su correo electrónico")
                })
                finish()
            }.addOnCanceledListener {
                Firebase.auth.signOut()
                Toast.makeText(this, "Error al crear la cuenta", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
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
}