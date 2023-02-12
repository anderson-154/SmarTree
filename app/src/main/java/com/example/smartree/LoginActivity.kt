package com.example.smartree

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.smartree.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onGoogle)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Permissions
        requestPermissions(arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ,1)

        //init
        setup()
        loadSession()
    }

    private fun setup(){
        binding.signInBtn.setOnClickListener{
            if(binding.emailSignInET.text.toString() != "" && binding.passwordSignInET.editText!!.text.toString() != ""){
                val email = binding.emailSignInET.text.toString()
                Firebase.auth.signInWithEmailAndPassword(
                    email,
                    binding.passwordSignInET.editText!!.text.toString()
                ).addOnSuccessListener {
                    showHome(email, ProviderType.BASIC)
                }.addOnFailureListener{
                    Toast.makeText(this,it.message, Toast.LENGTH_LONG).show()
                }
            }else {
                Toast.makeText(this, "Existen campos vacios", Toast.LENGTH_LONG).show()
            }
        }

        binding.signUpTxt.setOnClickListener {
            startActivity(Intent(this,RegistrationActivity::class.java))
            finish()
        }

        binding.googleBtn.setOnClickListener{
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this,googleConf)
            googleClient.signOut()
            launcher.launch(googleClient.signInIntent)

        }
    }

    private fun loadSession(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if(email!=null && provider!=null){
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun showHome(email:String, provider:ProviderType ){
        val intent = Intent(this, NavigationActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("provider", provider.name)
        startActivity(intent)
        finish()
    }

    fun onGoogle(result: ActivityResult){
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if(account != null){
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                Firebase.auth.signInWithCredential(credential).addOnSuccessListener {
                    showHome(account.email?:"", ProviderType.GOOGLE)
                }.addOnFailureListener{
                    showAlert()
                }
            }
        }catch (e:ApiException) {
            e.printStackTrace()
            showAlert()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            var allGrant = true
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) allGrant = false
            }
            if(!allGrant){
                Toast.makeText(this,"Por favor acepte los permisos", Toast.LENGTH_LONG).show()
            }
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