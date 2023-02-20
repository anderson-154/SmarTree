package com.example.smartree.model

import com.google.type.DateTime

data class Palm (
    var id: String ="",
    var uid: String = "",
    var name: String="",
    var type: String="",
    var place: String="",
    var sensorID: String="",
    var lat: Double=0.0,
    var lon: Double=0.0,
    var status: String="Disconnected",
    var millis: Long = System.currentTimeMillis()
)
