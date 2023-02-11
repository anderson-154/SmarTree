package com.example.smartree

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartree.model.Palm
import com.example.smartree.model.Sensor

class PalmAdapter : RecyclerView.Adapter<PalmViewHolder>() {

    private val palms = ArrayList<Palm>()
    lateinit var onClickPalmListener: PalmsFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PalmViewHolder {
      val inflater = LayoutInflater.from(parent.context)
      val view = inflater.inflate(R.layout.palm_row,parent,false)
      val palmViewHolder = PalmViewHolder(view)
      palmViewHolder.onclickPalmListener = onClickPalmListener
      return palmViewHolder
    }

    override fun onBindViewHolder(holder: PalmViewHolder, position: Int) {
        val palm = palms[position]
        holder.bindPalm(palm)
    }

    override fun getItemCount(): Int {
        return palms.size
    }

    fun clear() {
        val size = palms.size
        palms.clear()
        notifyItemRangeRemoved(0,size)
    }

    fun addPalm(palm: Palm){
        palms.add(palm)
        notifyItemInserted(palms.size-1)
    }

    interface OnClickSensorListener{
        fun openInfoSensor(id: String)
    }
}