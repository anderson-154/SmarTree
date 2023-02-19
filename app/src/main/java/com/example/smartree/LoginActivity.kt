package com.example.smartree

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.smartree.databinding.ActivityLoginBinding
import com.example.smartree.model.User
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onGoogle)
    private val callBackManager = CallbackManager.Factory.create()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //init
        setup()
    }

    private fun setup(){
        binding.signInBtn.setOnClickListener{
            if(binding.emailSignInET.text.toString() != "" && binding.passwordSignInET.editText!!.text.toString() != ""){
                val email = binding.emailSignInET.text.toString()
                Firebase.auth.signInWithEmailAndPassword(
                    email,
                    binding.passwordSignInET.editText!!.text.toString()
                ).addOnSuccessListener {
                    if(Firebase.auth.currentUser!!.isEmailVerified){
                        //Comprobar si el usuario ha completado el registro
                        Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                            val user = it.toObject(User::class.java)!!
                            if(user.names!=""){
                                showHome(email, ProviderType.BASIC)
                            }else{
                                showForm(email, ProviderType.BASIC)
                            }
                        }
                    }else{
                        showAlert("Correo invalido. Si ya creó una cuenta recuerde verificarla en su correo electrónico")
                    }

                }.addOnFailureListener{
                    showAlert()
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

        binding.facebookBtn.setOnClickListener{
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            LoginManager.getInstance().registerCallback(callBackManager,
                object : FacebookCallback<LoginResult>{
                    override fun onSuccess(result: LoginResult) {
                        val token = result.accessToken
                        val credential = FacebookAuthProvider.getCredential(token.token)
                        Firebase.auth.signInWithCredential(credential).addOnSuccessListener {
                            showHome(it.user?.email?:"", ProviderType.FACEBOOK)
                        }.addOnFailureListener{
                            showAlert()
                        }
                    }
                    override fun onCancel() {}

                    override fun onError(error: FacebookException) {
                        showAlert()
                    }
                })
        }

        binding.forgotPasswordTV.setOnClickListener{
            val email=binding.emailSignInET.text.toString()
            if(email!=""){
                Firebase.auth.sendPasswordResetEmail(email).addOnSuccessListener {
                    showAlert("Se ha enviado un método de recuperación a su correo")
                }.addOnCanceledListener {
                    showAlert()
                }
            }else{
                Toast.makeText(this, "Correo sin especificar", Toast.LENGTH_SHORT).show()
            }
        }

        val alert = intent.extras?.getString("alert", null)
        if(alert!=null){
            showAlert(alert)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        callBackManager.onActivityResult(requestCode, resultCode, data)
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

    private fun onGoogle(result: ActivityResult){
        binding.screen.visibility = View.GONE
        binding.gifImageView2.visibility = View.VISIBLE
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if(account != null){
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                Firebase.auth.signInWithCredential(credential).addOnSuccessListener {
                    val uid = Firebase.auth.currentUser!!.uid

                    Firebase.firestore.collection("users").document(uid).get().addOnSuccessListener {
                        var user = it.toObject(User::class.java)

                        //Comprobar si el usuario existe, y sino, crearlo
                        if (user==null){
                            user = User(uid, Firebase.auth.currentUser!!.email.toString())
                            Firebase.firestore.collection("users").document(uid).set(user).addOnSuccessListener {
                                Toast.makeText(this, "Usuario creado con exito", Toast.LENGTH_SHORT).show()
                            }
                        }

                        //Comprobar si el usuario a completado el registro
                        if(user.names!=""){
                            showHome(account.email?:"", ProviderType.GOOGLE)
                        }else{
                            showForm(account.email?:"", ProviderType.GOOGLE)
                        }
                    }
                }.addOnFailureListener{
                    binding.screen.visibility = View.VISIBLE
                    binding.gifImageView2.visibility = View.GONE
                    showAlert()
                }
            }
        }catch (e:ApiException) {
            e.printStackTrace()
            showAlert()
        }
    }

    private fun showAlert(msg:String="Se ha producido un error autenticando al usuario"){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("SmarTree")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}