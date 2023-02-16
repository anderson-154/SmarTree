package com.example.smartree


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smartree.databinding.ActivityProfileBinding
import com.example.smartree.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.logoutBtn.setOnClickListener{
            setResult(RESULT_OK)
            finish()
        }
            Firebase.firestore
                .collection("users").document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                    val user = it.toObject(User::class.java)!!
                    binding.nameTxt.text = user.name
                    binding.documentTxt.text = user.document
                    binding.phoneTxt.text = user.phone
                    binding.houseCampTxt.text = user.dpto
                }

        binding.backBtnProfile.setOnClickListener {
            startActivity(Intent(this,NavigationActivity::class.java))
            finish()
        }

        binding.editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
            finish()
        }
    }
}