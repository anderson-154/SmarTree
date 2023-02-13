package com.example.smartree

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.smartree.databinding.FragmentHomeBinding
import com.example.smartree.model.Statistics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment(onListener: OnCardListener) : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore
    private var listener = onListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setup()
        loadResults()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadResults()
    }

    private fun setup(){
        binding.totalCard.setOnClickListener{ listener.showPalms("all") }
        binding.healthyCard.setOnClickListener{ listener.showPalms("healthy") }
        binding.warningsCard.setOnClickListener{ listener.showPalms("warnings") }
        binding.dangerCard.setOnClickListener{ listener.showPalms("dangers") }
    }

    private fun loadResults(){
        var statistics:Statistics?
        val uid = Firebase.auth.currentUser?.uid.toString()
        db.collection("statistics").document(uid).get().addOnSuccessListener {
            statistics = it.toObject(Statistics::class.java)

            binding.totalTV.text = statistics?.total.toString()
            binding.healthyTV.text = statistics?.healthy.toString()
            binding.warnTV.text = statistics?.warning.toString()
            binding.wrongTV.text = statistics?.danger.toString()

        }.addOnCanceledListener {
            showAlert()
        }
    }

    companion object {
        fun newInstance(listener:OnCardListener) = HomeFragment(listener)
    }

    private fun showAlert(msg:String="Se ha producido un error autenticando al usuario"){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("SmarTree")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}

public interface OnCardListener{
    public fun showPalms(state:String)
}