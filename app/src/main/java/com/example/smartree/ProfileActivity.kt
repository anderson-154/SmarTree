package com.example.smartree


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.smartree.databinding.ActivityProfileBinding
import com.example.smartree.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileActivity : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.logoutBtn.setOnClickListener{
            Log.e("----------------->","Button")
            setResult(RESULT_OK)
            finish()
        }

        binding.backBtnProfile.setOnClickListener {
            startActivity(Intent(this,NavigationActivity::class.java))
            finish()
        }

        binding.editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
            finish()
        }

        loadProfile()
    }

    private fun loadProfile(){
        Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                val user = it.toObject(User::class.java)!!
                binding.nameTxt.text = user.names
                binding.lastNameTxt.text = user.lastName
                binding.documentTxt.text = user.document
                binding.phoneTxt.text = user.phone
                binding.dptoTxt.text = user.dpto
                binding.cityTxt.text = user.city
                binding.emailTxt.text = user.email
                if(user.uriProfile != ""){
                    Firebase.storage.reference.child("users_photos")
                        .child(user.uriProfile).downloadUrl.addOnSuccessListener {img->
                            Glide.with(binding.imgProfile).load(img).into(binding.imgProfile)
                        }
                }
        }
    }
}