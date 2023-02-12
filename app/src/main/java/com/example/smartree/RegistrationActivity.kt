package com.example.smartree

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.smartree.databinding.ActivityRegistrationBinding
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

        binding.signUpBtn.setOnClickListener {

            //Check if this account exists
            val email = binding.emailSignUpET.text.toString()
            val pass = binding.passwordSignUpET.editText!!.text.toString()

            //Check if are there empty fields
            if(binding.nameET.text.toString() != "" && binding.farmET.text.toString() != "" && binding.documentET.text.toString() != ""
                && email != "" && binding.phoneET.text.toString() != "" && pass != "" && binding.passwordConfirmET.editText!!.text.toString() != ""){

                //Check if password is equal to Confirm_Password
                if(pass == binding.passwordConfirmET.editText!!.text.toString()){

                        //
                        Firebase.auth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener {
                            Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_LONG).show()
                            registerUserData()
                        }.addOnFailureListener {
                            showAlert()
                        }

                }else{
                    Toast.makeText(this, "Los campos de contraseña deben coincidir", Toast.LENGTH_LONG).show()
                }

            }else{
                Toast.makeText(this, "Por favor llene todos los campos", Toast.LENGTH_LONG).show()
            }
        }

        binding.signInTxt.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }

    private fun registerUserData() {
        val uid = Firebase.auth.currentUser?.uid
        uid?.let {
            val user = User(
                it,
                binding.nameET.text.toString(),
                binding.farmET.text.toString(),
                binding.documentET.text.toString(),
                binding.emailSignUpET.text.toString(),
                binding.phoneET.text.toString(),
            )
            Log.e("---------->", "Antes de firestore")
            Firebase.firestore.collection("users").document(it).set(user).addOnSuccessListener {
                Log.e("---------->", "Firestore")
                Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }.addOnCanceledListener {
                Firebase.auth.signOut()
                Toast.makeText(this, "Error al crear la cuenta", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}