package com.example.smartree

import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

object Util {
    fun initRecycler(recycler: RecyclerView, activity: Activity, orientation: Int) : RecyclerView {
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(activity, orientation, false)
        return recycler
    }
}