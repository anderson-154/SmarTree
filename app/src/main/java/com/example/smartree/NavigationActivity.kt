package com.example.smartree


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.smartree.databinding.ActivityNavigationBinding
import com.facebook.login.LoginManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_navigation.*

enum class ProviderType {
    BASIC,
    GOOGLE,
    FACEBOOK
}

class NavigationActivity : AppCompatActivity() {

    private lateinit var sensorsFragment: SensorsFragment
    private lateinit var palmsFragment: PalmsFragment
    private lateinit var homeFragment: HomeFragment
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onLogout)

    private val binding: ActivityNavigationBinding by lazy {
        ActivityNavigationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        homeFragment = HomeFragment.newInstance()
        sensorsFragment = SensorsFragment.newInstance()
        palmsFragment = PalmsFragment.newInstance()

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        setup()
        showFragment(homeFragment)
    }

    private fun showFragment (fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.homeFragment.id, fragment)
        transaction.commit()
    }

    private fun setup(){
        binding.navigator.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.homemenu -> { showFragment(homeFragment) }
                R.id.sensorsmenu -> { showFragment(sensorsFragment) }
                R.id.treemenu -> { showFragment(palmsFragment) }

            }
            true
        }

        binding.profileButton.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            launcher.launch(intent)
        }
    }

    fun onLogout(result: ActivityResult){
        if(result.resultCode== RESULT_OK){
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            if(intent.extras?.getString("provider","")==ProviderType.FACEBOOK.name){
                LoginManager.getInstance().logOut()
            }

            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}