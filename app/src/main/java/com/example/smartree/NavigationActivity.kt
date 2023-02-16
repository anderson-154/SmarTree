package com.example.smartree


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.smartree.databinding.ActivityNavigationBinding
import com.example.smartree.databinding.FragmentPalmsBinding
import com.facebook.login.LoginManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_navigation.*

enum class ProviderType {
    BASIC,
    GOOGLE,
    FACEBOOK
}
@RequiresApi(Build.VERSION_CODES.M)
class NavigationActivity : AppCompatActivity(), OnCardListener {

    private var haveLocationPermissions = false
    private lateinit var sensorsFragment: SensorsFragment
    private lateinit var palmsFragment: PalmsFragment
    private lateinit var homeFragment: HomeFragment
    private lateinit var liveMapFragment: LiveMapFragment
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
        liveMapFragment = LiveMapFragment.newInstance()

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        setup()
        showFragment(homeFragment)
        askPermissions()
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
                R.id.mapmenu -> { showFragment(liveMapFragment) }
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

    override fun showPalms(state: String) {
        palmsFragment = PalmsFragment.newInstance(state)
        showFragment(palmsFragment)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        haveLocationPermissions = true
        for (result in grantResults) {
            haveLocationPermissions = haveLocationPermissions && (result!=-1)
        }

        if(!haveLocationPermissions) showAlert("Si no acepta los permisos la aplicación puede fallar")
    }

    private fun askPermissions(){
        //Permissions
        requestPermissions(arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION)
            ,1)
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