package com.example.smartree

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartree.model.Sensor

class SensorAdapter : RecyclerView.Adapter<SensorViewHolder>() {

    private val sensors = ArrayList<Sensor>()
    lateinit var onClickSensorListener: SensorsFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.sensor_row,parent,false)
        val sensorViewHolder = SensorViewHolder(view)
        sensorViewHolder.onClickSensorListener = onClickSensorListener
        return sensorViewHolder
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        val sensor = sensors[position]
        holder.bindSensor(sensor)
    }

    override fun getItemCount(): Int {
        return sensors.size
    }

    fun clear() {
        val size = sensors.size
        sensors.clear()
        notifyItemRangeRemoved(0,size)
    }

    fun addSensor(sensor: Sensor){
        sensors.add(sensor)
        notifyItemInserted(sensors.size-1)
    }

    interface OnClickSensorListener{
        fun openInfoSensor(id: String)
    }
}