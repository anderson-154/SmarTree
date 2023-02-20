package com.example.smartree

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.smartree.model.Sensor


class SensorViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {

    //STATE
    var sensor: Sensor? = null
    lateinit var onClickSensorListener: SensorsFragment

    //UI controllers
    var sensorName: TextView = itemView.findViewById(R.id.nameSensor)
    var sensorState: TextView = itemView.findViewById(R.id.statusSensorCard)
    var constraintLayout: CardView = itemView.findViewById(R.id.sensorCard)
    var sensorType: TextView = itemView.findViewById(R.id.sensorType)
    var sensorImg: ImageView = itemView.findViewById(R.id.imgSensorCard)

    init {
        constraintLayout.setOnClickListener {
            val id = sensor!!.serie
            onClickSensorListener.openInfoSensor(id)
        }
    }

    fun bindSensor(sensorBind: Sensor) {
        sensor = sensorBind
        sensorName.text = sensorBind.name
        sensorType.text = "Tipo: "+sensorBind.type
        checkStates(sensorBind.state)
    }

    private fun checkStates(state:String) {
        if(state=="Activo"){
            sensorState.text = "Activo"
            sensorState.setTextColor(Color.parseColor("#297012"))
            sensorImg.setImageResource(R.drawable.on)
        }else{
            sensorState.text = "Inactivo"
            sensorState.setTextColor(Color.RED)
            sensorImg.setImageResource(R.drawable.shutdown)
        }
    }
}