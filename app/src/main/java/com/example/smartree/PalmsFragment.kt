package com.example.smartree

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartree.databinding.FragmentPalmsBinding
import com.example.smartree.model.Palm
import com.example.smartree.model.Sensor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PalmsFragment : Fragment(), PalmAdapter.OnClickSensorListener  {
    private var _binding: FragmentPalmsBinding? = null
    private val binding get() = _binding!!
    val adapter = PalmAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPalmsBinding.inflate(inflater, container, false)
        Util.initRecycler(binding.palmsRecyclerView, requireActivity(), LinearLayoutManager.VERTICAL,).adapter = adapter
        adapter.clear()
        loadSensors()

        adapter.onClickPalmListener = this
        binding.addPalmFlotatingButton.setOnClickListener {
            startActivity(Intent(activity,PalmRegistrationActivity::class.java))
        }
        return binding.root
    }

    private fun loadSensors() {
        Firebase.firestore.collection("palms").get()
            .addOnCompleteListener { palm ->
                for (doc in palm.result!!) {
                    adapter.addPalm(doc.toObject(Palm::class.java))
                }
            }
    }

    companion object {
        fun newInstance() = PalmsFragment()
    }

    override fun openInfoSensor(id: String) {
        TODO("Not yet implemented")
    }

}