package com.example.smartree

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.smartree.databinding.FragmentMapsBinding
import com.example.smartree.model.Palm
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class MapsFragment(private val isOnlySelector:Boolean) : Fragment() {

    //Vars
    private lateinit var mMap:GoogleMap
    private lateinit var manager:LocationManager

    //Vars on selector Mode
    var palmMarker:Marker?=null
    private var idMarker:String = "user:${Firebase.auth.currentUser?.uid}palm:${UUID.randomUUID()}"

    //Listener on live map mode
    lateinit var listener:OnClickMarkerListener

    //Binding
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        manager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    //Interactions----------------------------------------------------------------------------------
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        //Click on map--------------------------------------------------------------------------
        mMap.setOnMapClickListener { pos->
            if(isOnlySelector){
                if(palmMarker!=null){
                    palmMarker?.position = pos
                }else{
                    palmMarker = putMarker(pos.latitude, pos.longitude)
                }
            }else{
                listener.hidePalmInfo()
            }
        }

        //Click on marker--------------------------------------------------------------------------
        mMap.setOnMarkerClickListener {marker->
            if(!isOnlySelector){
                listener.showPalmInfo(marker.snippet!!)
            }
            true
        }

        setInitialPos()
        loadPalms()
    }

    private fun loadPalms(){
        Firebase.firestore.collection("palms").whereEqualTo("uid",Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
            for(task in it){
                val palm = task.toObject(Palm::class.java)
                if(palm.lat!=0.0 || palm.lon!=0.0){
                    putMarker(palm.lat, palm.lon, palm.id)
                }
            }
        }.addOnFailureListener{
            Toast.makeText(activity, "Failed to load events", Toast.LENGTH_SHORT).show()
        }
    }

    private fun putMarker(lat:Double, lng:Double): Marker?{
        val pos = LatLng(lat, lng)
        return mMap.addMarker(MarkerOptions().position(pos))
    }

    private fun putMarker(lat:Double, lng:Double, id:String): Marker?{
        val pos = LatLng(lat, lng)
        return mMap.addMarker(MarkerOptions().position(pos).snippet(id))
    }

    @SuppressLint("MissingPermission")
    private fun setInitialPos(){
        var pos = LatLng(3.817998, -77.001362)
        val gsc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER )
        if(gsc!=null){
            pos = LatLng(gsc.latitude, gsc.longitude)
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos,15f))
    }

    interface OnClickMarkerListener{
        fun showPalmInfo(idMarker:String)
        fun hidePalmInfo()
    }
}