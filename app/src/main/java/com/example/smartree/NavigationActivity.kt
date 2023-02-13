package com.example.smartree


import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.smartree.databinding.ActivityNavigationBinding
import com.facebook.login.LoginManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

enum class ProviderType {
    BASIC,
    GOOGLE,
    FACEBOOK
}

class NavigationActivity : AppCompatActivity(), OnCardListener {

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

        homeFragment = HomeFragment.newInstance(this)
        sensorsFragment = SensorsFragment.newInstance()
        palmsFragment = PalmsFragment.newInstance("all")

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        setup()
        showFragment(homeFragment)

        binding.profileButton.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }
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
                R.id.treemenu -> {
                    palmsFragment = PalmsFragment.newInstance("all")
                    showFragment(palmsFragment)
                }

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
            val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
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

    override fun showPalms(state: String) {
        palmsFragment = PalmsFragment.newInstance(state)
        showFragment(palmsFragment)
    }
}