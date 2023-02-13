package com.example.smartree

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.smartree.model.Palm

class PalmViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

    var palm: Palm? = null
    lateinit var onclickPalmListener: PalmsFragment

    var palmName: TextView = itemView.findViewById(R.id.palmNameRow)
    var palmPlace: TextView = itemView.findViewById(R.id.placePalmRow)
    var palmCoordinates: TextView = itemView.findViewById(R.id.coordinatesPalmRow)
    var constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayoutRowPalm)
    var palmType: TextView = itemView.findViewById(R.id.typePalmText)
    var palmImg: ImageView = itemView.findViewById(R.id.imagePalmRow)


    init {
        constraintLayout.setOnClickListener {
            var id = palm!!.id
            onclickPalmListener.openInfoSensor(id)
        }
    }

    fun bindPalm(palmBind: Palm) {
        palm = palmBind
        palmName.setText(palmBind.name)
        palmCoordinates.setText(palmBind.coordinates)
        palmType.setText("Tipo: "+palmBind.type)

    }
}