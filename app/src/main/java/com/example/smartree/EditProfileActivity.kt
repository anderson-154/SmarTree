package com.example.smartree

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.smartree.databinding.ActivityEditProfileBinding
import com.example.smartree.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditProfileActivity : AppCompatActivity() {

    private val binding: ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

            Firebase.firestore
                .collection("users").document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                    val user = it.toObject(User::class.java)!!
                    binding.nameEditProfileET.setText(user.name)
                    binding.phoneEditProfileET.setText(user.phone)
                    binding.houseCampEditProfileET.setText(user.dpto)
                }

        binding.cameraButton.setOnClickListener {

        }

        binding.saveBtn.setOnClickListener {
            Firebase.firestore
                .collection("users").document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)!!
                    val uid = Firebase.auth.currentUser?.uid
                    uid?.let {
                        val newUser = User(
                            it,
                            user.email,
                            binding.houseCampEditProfileET.text.toString(),
                            user.document,
                            binding.nameEditProfileET.text.toString(),
                            binding.phoneEditProfileET.text.toString(),
                            //user.uriProfile
                        )
                        Firebase.firestore.collection("users").document(it).set(newUser)
                    }
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                }

            }


        binding.backBtnEditProfile.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
            finish()
        }
    }
}