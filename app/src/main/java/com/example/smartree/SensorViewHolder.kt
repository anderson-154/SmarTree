package com.example.smartree

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.smartree.model.Sensor


class SensorViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {

    //STATE
    var sensor: Sensor? = null
    lateinit var onClickSensorListener: SensorsFragment

    //UI controllers
    var sensorName: TextView = itemView.findViewById(R.id.sensorNameRow)
    var sensorState: TextView = itemView.findViewById(R.id.stateSensorRow)
    var constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayoutRow)
    var sensorType: TextView = itemView.findViewById(R.id.typeSensorText)
    var sensorImg: ImageView = itemView.findViewById(R.id.imageStateSensorRow)

    init {
        constraintLayout.setOnClickListener {
            var id = sensor!!.id
            onClickSensorListener.openInfoSensor(id)
        }
    }

    fun bindSensor(sensorBind: Sensor) {
        sensor = sensorBind
        sensorName.setText(sensorBind.name)
        sensorType.setText("Tipo: "+sensorBind.type)
        checkStates(sensorBind.state)
    }

    private fun checkStates(state:String) {
        if(state=="inactivo"){
            sensorState.setText("Inactivo")
            sensorState.setTextColor(Color.RED)
        }else{
            sensorState.setText("Activo")
            sensorState.setTextColor(Color.rgb(41,112,18))

        }
    }
}