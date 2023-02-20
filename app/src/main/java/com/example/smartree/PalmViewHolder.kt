package com.example.smartree

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.smartree.model.Palm

class PalmViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

    var palm: Palm? = null
    lateinit var listener: PalmAdapter.OnClickPalmListener

    var palmName: TextView = itemView.findViewById(R.id.nameCard)
    var palmPlace: TextView = itemView.findViewById(R.id.addressCard)
    private var card: CardView = itemView.findViewById(R.id.palmCard)
    var palmType: TextView = itemView.findViewById(R.id.typeCard)
    var palmStatus: TextView = itemView.findViewById(R.id.statusCard)
    var palmImg: ImageView = itemView.findViewById(R.id.imgCard)


    init {
        card.setOnClickListener {
            val id = palm!!.id
            listener.openPalmInfo(id)
        }
    }

    fun bindPalm(palmBind: Palm) {
        val status = palmBind.status
        palm = palmBind
        palmPlace.text = palmBind.place
        palmName.text = palmBind.name
        palmType.text = palmBind.type
        palmStatus.text = status
        if(status=="Saludable"){
            palmStatus.setTextColor(Color.parseColor("#297012"))
            palmImg.setImageResource(R.drawable.health)
        }else if(status=="Sospechoso"){
            palmStatus.setTextColor(Color.parseColor("#ff3e3e"))
            palmImg.setImageResource(R.drawable.warning)
        }else if(status=="Infectado"){
            palmStatus.setTextColor(Color.parseColor("#CD4242"))
            palmImg.setImageResource(R.drawable.bug)
        }else if(palm!!.sensorID!=""){
            palmStatus.text = "Enlazado"
            palmStatus.setTextColor(Color.parseColor("#A0E5E8"))
            palmImg.setImageResource(R.drawable.linked)
        }
        else{
            palmStatus.setTextColor(Color.parseColor("#CD4242"))
            palmImg.setImageResource(R.drawable.off)
        }
    }
}