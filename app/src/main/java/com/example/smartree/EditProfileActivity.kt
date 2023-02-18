package com.example.smartree

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.R
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.smartree.databinding.ActivityEditProfileBinding
import com.example.smartree.databinding.DialogImgProfileBinding
import com.example.smartree.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID


class EditProfileActivity : AppCompatActivity() {

    private var URI:String = ""
    private var file:File? = null

    private val binding: ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(layoutInflater)
    }
    private val bindingDialog: DialogImgProfileBinding by lazy {
        DialogImgProfileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

            Firebase.firestore
                .collection("users").document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                    val user = it.toObject(User::class.java)!!
                    binding.nameEditProfileET.setText(user.names)
                    binding.phoneEditProfileET.setText(user.phone)
                    binding.lastNameEditProfileET.setText(user.lastName)
                    binding.cityEditProfileET.setText(user.city)

                    var dptos = ArrayList<String>()
                    dptos.add("Departamento")
                    dptos.add("Amazonas")
                    dptos.add("Antioquia")
                    dptos.add("Arauca")
                    dptos.add("Atlántico")
                    dptos.add("Bolívar")
                    dptos.add("Boyacá")
                    dptos.add("Caldas")
                    dptos.add("Caquetá")
                    dptos.add("Casanare")
                    dptos.add("Cauca")
                    dptos.add("Cesar")
                    dptos.add("Chocó")
                    dptos.add("Córdoba")
                    dptos.add("Cundinamarca")
                    dptos.add("Guainía")
                    dptos.add("Guaviare")
                    dptos.add("Huila")
                    dptos.add("La Guajira")
                    dptos.add("Magdalena")
                    dptos.add("Meta")
                    dptos.add("Nariño")
                    dptos.add("Norte de Santander")
                    dptos.add("Putumayo")
                    dptos.add("Quindío")
                    dptos.add("Risaralda")
                    dptos.add("Santander")
                    dptos.add("Sucre")
                    dptos.add("Tolima")
                    dptos.add("Valle del Cauca")
                    dptos.add("Vaupés")
                    dptos.add("Vichada")

                    var spinnerDptosAdapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,dptos)

                    binding.spinnerDptoEditProfile.setAdapter(spinnerDptosAdapter)

                    if(dptos.contains(user.dpto)){
                        val indice = dptos.indexOf(user.dpto)
                        binding.spinnerDptoEditProfile.setSelection(indice)
                    }

                    if(!user.uriProfile.equals("")){
                        Firebase.storage.reference.child("users_photos").child(user.uriProfile).downloadUrl.addOnSuccessListener {
                            Glide.with(binding.imgProfile).load(it).into(binding.imgProfile)
                        }
                    }

                }


        val cameralauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onCameraResult)
        val gallerylauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onGalleryResult)

        binding.cameraButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setView(com.example.smartree.R.layout.dialog_img_profile)
            val dialog = builder.create()
            dialog.setTitle("Cambia tu foto de perfil")
            dialog.setIcon(com.example.smartree.R.drawable.caficultor)
            dialog.show()

            var camera: Button = dialog.findViewById(com.example.smartree.R.id.cameraBtnDialog)
            camera.setOnClickListener {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                file = File("${getExternalFilesDir(null)}/photo.png")
                val uri = FileProvider.getUriForFile(this, packageName,file!!)
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri)
                URI = uri.toString()
                cameralauncher.launch(intent)
                dialog.dismiss()
            }


            var gallery: Button = dialog.findViewById(com.example.smartree.R.id.galleryBtnDialog)
            gallery.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                gallerylauncher.launch(intent)
                dialog.dismiss()
            }
        }

        binding.saveBtn.setOnClickListener {
            Firebase.firestore
                .collection("users").document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)!!
                    val uid = Firebase.auth.currentUser?.uid
                    val filename = UUID.randomUUID().toString()
                    uid?.let {
                        val newUser = User(
                            it,
                            user.email,
                            user.type,
                            user.document,
                            binding.nameEditProfileET.text.toString(),
                            binding.lastNameEditProfileET.text.toString(),
                            binding.phoneEditProfileET.text.toString(),
                            user.address,
                            binding.cityEditProfileET.text.toString(),
                            binding.spinnerDptoEditProfile.selectedItem.toString(),
                            filename
                        )
                        if(URI!="") Firebase.storage.reference.child("users_photos").child(filename).putFile(Uri.parse(URI)).addOnSuccessListener{
                            startActivity(Intent(this, ProfileActivity::class.java))
                            finish()
                        }
                        else newUser.uriProfile = user.uriProfile
                        Firebase.firestore.collection("users").document(it).set(newUser)
                    }

                }

            }


        binding.backBtnEditProfile.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
            finish()
        }
    }

    fun onCameraResult(result: ActivityResult){
        if(result.resultCode == Activity.RESULT_OK){
            val bitmap = BitmapFactory.decodeFile(file?.path)
            val thumball = Bitmap.createScaledBitmap(bitmap, bitmap.width/4,bitmap.height/4,true)
            val uriImage = Uri.parse(URI)
            binding.imgProfile.setImageURI(uriImage)
        }else if(result.resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(this,"No tomo la foto", Toast.LENGTH_SHORT).show()
        }
    }

    fun onGalleryResult(result: ActivityResult){
        if(result.resultCode == Activity.RESULT_OK){
            URI = result.data?.data.toString()
            val uriImage = Uri.parse(URI)
            binding.imgProfile.setImageURI(uriImage)
        }
    }
}