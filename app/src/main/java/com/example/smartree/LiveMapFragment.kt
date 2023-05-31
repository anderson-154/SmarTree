package com.example.smartree

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartree.databinding.FragmentLiveMapBinding
import com.example.smartree.model.Palm
import com.example.smartree.model.Sensor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LiveMapFragment : Fragment (), MapsFragment.OnClickMarkerListener {

    //Binding
    private var _binding: FragmentLiveMapBinding? = null
    private val binding get() = _binding!!

    //Fragment GoogleMaps
    private lateinit var mapsFragment: MapsFragment

    //Database
    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLiveMapBinding.inflate(inflater, container, false)

        //Create Fragment
        mapsFragment = MapsFragment(false)
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.mapConteiner, mapsFragment)
        transaction?.commit()

        //Subscribe listener
        mapsFragment.listener = this

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = LiveMapFragment()
    }

    override fun showPalmInfo(idMarker: String) {

        //Get Event by Id
        db.collection("palms").document(idMarker).get().addOnSuccessListener { document ->

            val palm = document.toObject(Palm::class.java)!!

            binding.infoContainer.visibility = View.VISIBLE

            binding.statePinTV.text = palm.status
            if(palm.status=="Saludable"){
                binding.statePinTV.setTextColor(resources.getColor(R.color.dark_green))
                binding.iconPin.setImageResource(R.drawable.health)
            }else if(palm.status=="Sospechoso"){
                binding.statePinTV.setTextColor(resources.getColor(R.color.light_orange))
                binding.iconPin.setImageResource(R.drawable.warning)
            }else if(palm.status=="Infectado"){
                binding.statePinTV.setTextColor(resources.getColor(R.color.red))
                binding.iconPin.setImageResource(R.drawable.bug)
            }

            //Set names
            binding.namePinTV.text = palm.name
            binding.addressPinTV.text = palm.place

            //Request join to club
            binding.infoContainer.setOnClickListener{
                val intent = Intent(activity, PalmEditActivity::class.java)
                intent.putExtra("palmId", palm.id)
                startActivity(intent)
            }
        }
    }

    override fun hidePalmInfo() {
        binding.infoContainer.visibility = View.GONE
    }
}