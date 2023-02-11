package com.example.smartree

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartree.databinding.FragmentSensorsBinding
import com.example.smartree.model.Sensor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SensorsFragment : Fragment(), SensorAdapter.OnClickSensorListener  {
    private var _binding: FragmentSensorsBinding? = null
    private val binding get() = _binding!!
    val adapter = SensorAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSensorsBinding.inflate(inflater, container, false)
        Util.initRecycler(binding.sensorsRecyclerView, requireActivity(), LinearLayoutManager.VERTICAL,).adapter = adapter
        adapter.clear()
        loadSensors()

        adapter.onClickSensorListener = this
        binding.addSensorFlotatingButton.setOnClickListener {
            startActivity(Intent(activity,SensorRegistrationActivity::class.java))
        }
        return binding.root
    }

    private fun loadSensors() {
        Firebase.firestore.collection("sensors").get()
            .addOnCompleteListener { sensor ->
                for (doc in sensor.result!!) {
                    adapter.addSensor(doc.toObject(Sensor::class.java))
                }
            }
    }

    companion object {
        fun newInstance() = SensorsFragment()
    }

    override fun openInfoSensor(id: String) {
        TODO("Not yet implemented")
    }

}