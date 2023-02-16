package com.example.smartree.model

data class Palm (
    var id: String ="",
    var name: String="",
    var type: String="",
    val sensor: String="",
    var lat: Double=0.0,
    var lon: Double=0.0,
)
