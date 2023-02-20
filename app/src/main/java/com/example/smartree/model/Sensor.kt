package com.example.smartree.model

data class Sensor (
    var serie: String ="",
    var uid: String ="",
    var name: String="",
    var type: String="",
    var state: String="",
    var linked: Boolean=false
)