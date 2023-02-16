package com.example.smartree.model

data class Palm (
    var id: String ="",
    var uid: String = "",
    var name: String="",
    var address: String="",
    var type: String="",
    val sensorID: String="",
    var lat: Double=0.0,
    var lon: Double=0.0,
)
