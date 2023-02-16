package com.example.smartree

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartree.databinding.FragmentPalmsBinding
import com.example.smartree.model.Palm
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PalmsFragment : Fragment(), PalmAdapter.OnClickPalmListener  {
    private var filter:String = "all"
    private var _binding: FragmentPalmsBinding? = null
    private val binding get() = _binding!!
    private val adapter = PalmAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPalmsBinding.inflate(inflater, container, false)
        Util.initRecycler(binding.palmsRecyclerView, requireActivity(), LinearLayoutManager.VERTICAL,).adapter = adapter
        adapter.setListener(this)
        adapter.clear()
        setTitle()
        loadPalms()

        binding.addPalmFlotatingButton.setOnClickListener {
            startActivity(Intent(activity,PalmRegistrationActivity::class.java))
        }
        return binding.root
    }

    private fun loadPalms() {
        if(filter!="all"){
            Firebase.firestore.collection("palms")
                .whereEqualTo("uid", Firebase.auth.currentUser!!.uid).whereEqualTo("status", filter).get()
                .addOnCompleteListener { list ->
                    for (doc in list.result!!) {
                        val palm = doc.toObject(Palm::class.java)
                        adapter.addPalm(palm)
                    }
                }
        }else{
            Firebase.firestore.collection("palms").whereEqualTo("uid", Firebase.auth.currentUser!!.uid).get()
                .addOnCompleteListener { list ->
                    for (doc in list.result!!) {
                        adapter.addPalm(doc.toObject(Palm::class.java))
                    }
                }
        }
    }

    private fun setTitle(){
        if(filter!="all"){
            if(filter=="Saludable"){
                binding.palmsTitle.text = "Palmas Saludables"
                binding.palmsTitle.setTextColor(resources.getColor(R.color.dark_green))
            }else if(filter=="Sospechoso"){
                binding.palmsTitle.text = "Palmas Sospechosas"
                binding.palmsTitle.setTextColor(resources.getColor(R.color.light_orange))
            }else if(filter=="Infectado"){
                binding.palmsTitle.text = "Palmas Infectadas"
                binding.palmsTitle.setTextColor(resources.getColor(R.color.red))
            }else{
                binding.palmsTitle.text = "Error"
            }
        }
    }

    companion object {
        fun newInstance() = PalmsFragment()
    }

    override fun openPalmInfo(id: String) {
        val intent = Intent(activity, ProfileActivity::class.java)
        intent.putExtra("palmID", id)
        startActivity(intent)
    }

    fun setFilter(filter:String){
        this.filter = filter
    }
}