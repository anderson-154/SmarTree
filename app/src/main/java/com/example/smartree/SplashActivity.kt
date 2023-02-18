package com.example.smartree

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smartree.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        loadSession()
    }

    private fun loadSession(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if(email!=null && provider!=null){
            Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                val user = it.toObject(User::class.java)!!
                if(user.names!=""){
                    showHome(email, ProviderType.valueOf(provider))
                }else{
                    showForm(email, ProviderType.valueOf(provider))
                }
            }
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showHome(email:String, provider:ProviderType ){
        val intent = Intent(this, NavigationActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("provider", provider.name)
        startActivity(intent)
        finish()
    }

    private fun showForm(email:String, provider:ProviderType){
        val intent = Intent(this, RegistrationAfterActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("provider", provider.name)
        startActivity(intent)
        finish()
    }
}